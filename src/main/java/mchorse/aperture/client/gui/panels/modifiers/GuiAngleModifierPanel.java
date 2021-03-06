package mchorse.aperture.client.gui.panels.modifiers;

import mchorse.aperture.camera.modifiers.AngleModifier;
import mchorse.aperture.client.gui.GuiModifiersManager;
import mchorse.mclib.client.gui.widgets.GuiTrackpad;
import mchorse.mclib.client.gui.widgets.GuiTrackpad.ITrackpadListener;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;

public class GuiAngleModifierPanel extends GuiAbstractModifierPanel<AngleModifier> implements ITrackpadListener
{
    public GuiTrackpad yaw;
    public GuiTrackpad pitch;
    public GuiTrackpad roll;
    public GuiTrackpad fov;

    public GuiAngleModifierPanel(AngleModifier modifier, GuiModifiersManager modifiers, FontRenderer font)
    {
        super(modifier, modifiers, font);

        this.yaw = new GuiTrackpad(this, font);
        this.yaw.title = I18n.format("aperture.gui.panels.yaw");

        this.pitch = new GuiTrackpad(this, font);
        this.pitch.title = I18n.format("aperture.gui.panels.pitch");

        this.roll = new GuiTrackpad(this, font);
        this.roll.title = I18n.format("aperture.gui.panels.roll");

        this.fov = new GuiTrackpad(this, font);
        this.fov.title = I18n.format("aperture.gui.panels.fov");
    }

    @Override
    public void setTrackpadValue(GuiTrackpad trackpad, float value)
    {
        if (trackpad == this.yaw)
        {
            this.modifier.angle.yaw = value;
        }
        else if (trackpad == this.pitch)
        {
            this.modifier.angle.pitch = value;
        }
        else if (trackpad == this.roll)
        {
            this.modifier.angle.roll = value;
        }
        else if (trackpad == this.fov)
        {
            this.modifier.angle.fov = value;
        }

        this.modifiers.editor.updateProfile();
    }

    @Override
    public void update(int x, int y, int w)
    {
        super.update(x, y, w);

        int width = (w - 20) / 2;

        this.yaw.update(x + 5, y + 25, width, 20);
        this.pitch.update(x + w - width - 5, y + 25, width, 20);
        this.roll.update(x + 5, y + 50, width, 20);
        this.fov.update(x + w - width - 5, y + 50, width, 20);

        this.yaw.setValue(this.modifier.angle.yaw);
        this.pitch.setValue(this.modifier.angle.pitch);
        this.roll.setValue(this.modifier.angle.roll);
        this.fov.setValue(this.modifier.angle.fov);
    }

    @Override
    public int getHeight()
    {
        return 75;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        this.yaw.mouseClicked(mouseX, mouseY, mouseButton);
        this.pitch.mouseClicked(mouseX, mouseY, mouseButton);
        this.roll.mouseClicked(mouseX, mouseY, mouseButton);
        this.fov.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        this.yaw.mouseReleased(mouseX, mouseY, state);
        this.pitch.mouseReleased(mouseX, mouseY, state);
        this.roll.mouseReleased(mouseX, mouseY, state);
        this.fov.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        this.yaw.keyTyped(typedChar, keyCode);
        this.pitch.keyTyped(typedChar, keyCode);
        this.roll.keyTyped(typedChar, keyCode);
        this.fov.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean hasActiveTextfields()
    {
        return this.yaw.text.isFocused() || this.pitch.text.isFocused() || this.roll.text.isFocused() || this.fov.text.isFocused();
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        super.draw(mouseX, mouseY, partialTicks);

        this.yaw.draw(mouseX, mouseY, partialTicks);
        this.pitch.draw(mouseX, mouseY, partialTicks);
        this.roll.draw(mouseX, mouseY, partialTicks);
        this.fov.draw(mouseX, mouseY, partialTicks);
    }
}