package net.pedroksl.advanced_ae.gui.quantumcomputer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import appeng.api.config.CpuSelectionMode;
import appeng.api.stacks.AmountFormat;
import appeng.api.stacks.GenericStack;
import appeng.client.Point;
import appeng.client.gui.ICompositeWidget;
import appeng.client.gui.Icon;
import appeng.client.gui.Tooltip;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.Color;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.Scrollbar;
import appeng.core.definitions.AEParts;
import appeng.core.localization.ButtonToolTips;
import appeng.core.localization.GuiText;
import appeng.core.localization.Tooltips;

public class AdvCpuSelectionList implements ICompositeWidget {
    private static final int ROWS = 6;

    private final Blitter background;
    private final Blitter buttonBg;
    private final QuantumComputerMenu menu;
    private final Color textColor;
    private final int selectedColor;
    private final Scrollbar scrollbar;
    private Rect2i bounds = new Rect2i(0, 0, 0, 0);

    public AdvCpuSelectionList(QuantumComputerMenu menu, Scrollbar scrollbar, ScreenStyle style) {
        this.menu = menu;
        this.scrollbar = scrollbar;
        this.background = style.getImage("cpuList");
        this.buttonBg = style.getImage("cpuListButton");
        this.textColor = style.getColor(PaletteColor.DEFAULT_TEXT_COLOR);
        this.selectedColor = style.getColor(PaletteColor.SELECTION_COLOR).toARGB();
        this.scrollbar.setCaptureMouseWheel(false);
    }

    @Override
    public void setPosition(Point position) {
        this.bounds = new Rect2i(position.getX(), position.getY(), bounds.getWidth(), bounds.getHeight());
    }

    @Override
    public void setSize(int width, int height) {
        this.bounds = new Rect2i(bounds.getX(), bounds.getY(), width, height);
    }

    @Override
    public Rect2i getBounds() {
        return bounds;
    }

    @Override
    public boolean onMouseWheel(Point mousePos, double delta) {
        scrollbar.onMouseWheel(mousePos, delta);
        return true;
    }

    @Nullable
    @Override
    public Tooltip getTooltip(int mouseX, int mouseY) {
        QuantumComputerMenu.CraftingCpuListEntry cpu = hitTestCpu(new Point(mouseX, mouseY));
        if (cpu != null) {
            ArrayList<Component> tooltipLines = new ArrayList<Component>();
            tooltipLines.add(getCpuName(cpu));

            int coProcessors = cpu.coProcessors();
            if (coProcessors == 1) {
                tooltipLines.add(ButtonToolTips.CpuStatusCoProcessor.text(Tooltips.ofNumber(coProcessors))
                        .withStyle(ChatFormatting.GRAY));
            } else if (coProcessors > 1) {
                tooltipLines.add(ButtonToolTips.CpuStatusCoProcessors.text(Tooltips.ofNumber(coProcessors))
                        .withStyle(ChatFormatting.GRAY));
            }

            tooltipLines.add(ButtonToolTips.CpuStatusStorage.text(Tooltips.ofBytes(cpu.storage()))
                    .withStyle(ChatFormatting.GRAY));

            Component modeText = null;
            switch (cpu.mode()) {
                case PLAYER_ONLY:
                    modeText = ButtonToolTips.CpuSelectionModePlayersOnly.text();
                    break;
                case MACHINE_ONLY:
                    modeText = ButtonToolTips.CpuSelectionModeAutomationOnly.text();
                    break;
                default:
                    break;
            }
            if (modeText != null) {
                tooltipLines.add(modeText);
            }

            GenericStack currentJob = cpu.currentJob();
            if (currentJob != null) {
                tooltipLines.add(ButtonToolTips.CpuStatusCrafting.text(Tooltips.ofAmount(currentJob))
                        .append(" ")
                        .append(currentJob.what().getDisplayName()));
                tooltipLines.add(ButtonToolTips.CpuStatusCraftedIn.text(
                        Tooltips.ofPercent(cpu.progress()),
                        Tooltips.ofDuration(cpu.elapsedTimeNanos(), TimeUnit.NANOSECONDS)));
            }
            return new Tooltip(tooltipLines);
        }
        return null;
    }

    @Override
    public boolean onMouseUp(Point mousePos, int button) {
        QuantumComputerMenu.CraftingCpuListEntry cpu = hitTestCpu(mousePos);
        if (cpu != null) {
            menu.selectCpu(cpu.serial());
            return true;
        }
        return false;
    }

    @Nullable
    private QuantumComputerMenu.CraftingCpuListEntry hitTestCpu(Point mousePos) {
        int relX = mousePos.getX() - bounds.getX();
        int relY = mousePos.getY() - bounds.getY();
        relX -= 8;
        if (relX < 0 || relX >= buttonBg.getSrcWidth()) {
            return null;
        }

        relY -= 19;
        int buttonIdx = scrollbar.getCurrentScroll() + relY / (buttonBg.getSrcHeight() + 1);
        if (relY % (buttonBg.getSrcHeight() + 1) == buttonBg.getSrcHeight()) {
            return null;
        }
        if (relY < 0 || buttonIdx >= menu.cpuList.cpus().size()) {
            return null;
        }

        List<QuantumComputerMenu.CraftingCpuListEntry> cpus = menu.cpuList.cpus();
        if (buttonIdx >= 0 && buttonIdx < cpus.size()) {
            return cpus.get(buttonIdx);
        }
        return null;
    }

    @Override
    public void updateBeforeRender() {
        int hiddenRows = Math.max(0, menu.cpuList.cpus().size() - ROWS);
        scrollbar.setRange(0, hiddenRows, ROWS / 3);
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        int x = bounds.getX() + this.bounds.getX();
        int y = bounds.getY() + this.bounds.getY();
        background.dest(x, y, this.bounds.getWidth(), this.bounds.getHeight()).blit(guiGraphics);

        x += 9;
        y += 19;

        List<QuantumComputerMenu.CraftingCpuListEntry> cpus = menu.cpuList
                .cpus()
                .subList(
                        Mth.clamp(scrollbar.getCurrentScroll(), 0, menu.cpuList.cpus().size()),
                        Mth.clamp(scrollbar.getCurrentScroll() + ROWS, 0, menu.cpuList.cpus().size()));
        for (QuantumComputerMenu.CraftingCpuListEntry cpu : cpus) {
            int color = -1;
            if (cpu.serial() == menu.getSelectedCpuSerial()) {
                color = selectedColor;
            }
            buttonBg.dest(x, y).colorRgb(color).blit(guiGraphics);

            Component name = getCpuName(cpu);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(x + 3, y + 3, 0);
            guiGraphics.pose().scale(0.8f, 0.8f, 1);
            guiGraphics.drawString(Minecraft.getInstance().font, name, 0, 0, textColor.toARGB(), false);
            guiGraphics.pose().popPose();

            InfoBar infoBar = new InfoBar();
            GenericStack currentJob = cpu.currentJob();
            if (currentJob != null) {
                infoBar.add(Icon.CRAFT_HAMMER, 0.6f);
                infoBar.addSpace(2);
                String craftAmt = currentJob.what().formatAmount(currentJob.amount(), AmountFormat.SLOT);
                infoBar.add(craftAmt, textColor.toARGB(), 0.6f);
                infoBar.addSpace(1);
                infoBar.add(currentJob.what(), 0.6f);

                int progress = (int) (cpu.progress() * (buttonBg.getSrcWidth() - 1));
                guiGraphics.fill(
                        x + 1,
                        y + buttonBg.getSrcHeight() - 2,
                        x + progress,
                        y + buttonBg.getSrcHeight() - 1,
                        menu.getSelectedCpuSerial() == cpu.serial() ? 0xFFFFFFFF : (0xFF000000 + selectedColor));
            } else {
                infoBar.add(Icon.LEVEL_ITEM, 0.6f);
                infoBar.addSpace(1);
                infoBar.add(formatStorage(cpu), textColor.toARGB(), 0.6f);
                infoBar.addSpace(1);

                if (cpu.coProcessors() > 0) {
                    infoBar.add(Icon.BLOCKING_MODE_NO, 0.6f);
                    infoBar.add(String.valueOf(cpu.coProcessors()), textColor.toARGB(), 0.6f);
                    infoBar.addSpace(1);
                }

                switch (cpu.mode()) {
                    case PLAYER_ONLY:
                        infoBar.add(AEParts.TERMINAL, 0.6f);
                        break;
                    case MACHINE_ONLY:
                        infoBar.add(AEParts.EXPORT_BUS, 0.6f);
                        break;
                    default:
                        break;
                }
            }

            infoBar.render(guiGraphics, x + 2, y + buttonBg.getSrcHeight() - 12);
            y += buttonBg.getSrcHeight() + 1;
        }
    }

    private String formatStorage(QuantumComputerMenu.CraftingCpuListEntry cpu) {
        long storage = cpu.storage();
        int unit = -1;
        while (storage > 1024) {
            storage /= 1024;
            unit++;
        }

        String suffix;
        switch (unit) {
            case 0:
                suffix = "k";
                break;
            case 1:
                suffix = "M";
                break;
            case 2:
                suffix = "G";
                break;
            default:
                suffix = "T";
                break;
        }
        return storage + suffix;
    }

    private Component getCpuName(QuantumComputerMenu.CraftingCpuListEntry cpu) {
        return cpu.name() != null ? cpu.name() : GuiText.CPUs.text().append(String.format(" #%d", cpu.serial()));
    }
}
