package mchorse.aperture.client.gui.panels;

import mchorse.aperture.camera.fixtures.LookFixture;
import mchorse.aperture.client.gui.panels.modules.GuiPointModule;
import mchorse.aperture.client.gui.panels.modules.GuiTargetModule;
import mchorse.mclib.client.gui.widgets.GuiTrackpad;
import mchorse.mclib.client.gui.widgets.GuiTrackpad.ITrackpadListener;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Look fixture panel
 *
 * This panel is responsible for editing look fixture's point and target entity.
 * Target entity can be specified either using a selector or UUID.
 */
public class GuiLookFixturePanel extends GuiAbstractFixturePanel<LookFixture> implements ITrackpadListener
{
    public GuiPointModule point;
    public GuiTargetModule target;

    public GuiLookFixturePanel(FontRenderer font)
    {
        super(font);

        this.point = new GuiPointModule(this, font);
        this.target = new GuiTargetModule(this, font, -1);
        this.height = 100;
    }

    @Override
    public void setTrackpadValue(GuiTrackpad trackpad, float value)
    {
        if (trackpad == this.point.x)
        {
            this.fixture.position.point.x = trackpad.value;
        }
        else if (trackpad == this.point.y)
        {
            this.fixture.position.point.y = trackpad.value;
        }
        else if (trackpad == this.point.z)
        {
            this.fixture.position.point.z = trackpad.value;
        }

        super.setTrackpadValue(trackpad, value);
    }

    @Override
    public void setEntryValue(int id, String value)
    {
        if (id == -1)
        {
            this.fixture.selector = value;
            this.fixture.tryFindingEntity();
        }

        super.setEntryValue(id, value);
    }

    @Override
    public void update(GuiScreen screen)
    {
        super.update(screen);

        int x = this.area.x + this.area.w - 80;
        int y = this.area.y + 10;

        this.point.update(x, y);

        x = this.area.x;
        y = this.area.y + 60;

        this.target.update(x, y);
    }

    @Override
    public void editFixture(EntityPlayer entity)
    {
        this.fixture.position.set(entity);

        super.editFixture(entity);
    }

    @Override
    public void select(LookFixture fixture, long duration)
    {
        super.select(fixture, duration);

        this.point.fill(fixture.position.point);
        this.target.fill(fixture);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        this.point.mouseClicked(mouseX, mouseY, mouseButton);
        this.target.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);

        this.point.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean hasActiveTextfields()
    {
        return super.hasActiveTextfields() || this.target.hasActiveTextfields();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        super.keyTyped(typedChar, keyCode);

        this.point.keyTyped(typedChar, keyCode);
        this.target.keyTyped(typedChar, keyCode);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        super.draw(mouseX, mouseY, partialTicks);

        this.editor.drawCenteredString(this.font, I18n.format("aperture.gui.panels.position"), this.point.x.area.x + this.point.x.area.w / 2, this.point.x.area.y - 14, 0xffffffff);

        this.point.draw(mouseX, mouseY, partialTicks);
        this.target.draw(mouseX, mouseY, partialTicks);
    }
}