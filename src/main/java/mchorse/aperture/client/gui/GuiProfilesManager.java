package mchorse.aperture.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import mchorse.aperture.ClientProxy;
import mchorse.aperture.camera.CameraAPI;
import mchorse.aperture.camera.CameraControl;
import mchorse.aperture.camera.CameraProfile;
import mchorse.aperture.camera.destination.AbstractDestination;
import mchorse.aperture.camera.destination.ClientDestination;
import mchorse.aperture.camera.destination.ServerDestination;
import mchorse.aperture.client.gui.panels.IGuiModule;
import mchorse.aperture.client.gui.utils.GuiUtils;
import mchorse.aperture.network.Dispatcher;
import mchorse.aperture.network.common.PacketRequestCameraProfiles;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.ScrollArea;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

/**
 * Camera profile manager GUI
 * 
 * This GUI is responsible managing currently loaded and possible for loading 
 * camera profiles. 
 */
public class GuiProfilesManager implements IGuiModule
{
    private Minecraft mc = Minecraft.getMinecraft();

    public Area rect = new Area();
    public ScrollArea scrollLoaded = new ScrollArea(20);
    public ScrollArea scrollLoad = new ScrollArea(20);
    public GuiCameraEditor editor;
    public boolean visible;
    public boolean showLoaded = true;
    public boolean rename = false;
    public boolean error = false;
    public List<AbstractDestination> destToLoad = new ArrayList<AbstractDestination>();

    public GuiButton loaded;
    public GuiButton load;
    public GuiButton add;

    public GuiTextField name;

    public GuiProfilesManager(GuiCameraEditor editor)
    {
        this.editor = editor;

        this.loaded = new GuiButton(1, 0, 0, I18n.format("aperture.gui.profiles.loaded"));
        this.load = new GuiButton(2, 0, 0, I18n.format("aperture.gui.profiles.load"));
        this.add = new GuiButton(3, 0, 0, I18n.format("aperture.gui.profiles.new"));

        this.name = new GuiTextField(0, this.mc.fontRendererObj, 0, 0, 0, 0);
    }

    public void init()
    {
        this.destToLoad.clear();
        this.rename = false;

        for (String filename : CameraAPI.getClientProfiles())
        {
            this.destToLoad.add(new ClientDestination(filename));
        }

        Dispatcher.sendToServer(new PacketRequestCameraProfiles());
    }

    public void update(int x, int y, int w, int h)
    {
        this.rect.set(x, y, w, h);
        this.scrollLoad.set(x + 5, y + 30, w - 10, h - 60);
        this.scrollLoaded.set(x + 5, y + 30, w - 10, h - 60);
        this.scrollLoaded.setSize(ClientProxy.control.profiles.size());

        int span = (w - 12) / 2;

        GuiUtils.setSize(this.loaded, x + 5, y + 5, span, 20);
        GuiUtils.setSize(this.load, x + span + 7, y + 5, span, 20);

        GuiUtils.setSize(this.add, x + w - 45, y + h - 25, 40, 20);
        GuiUtils.setSize(this.name, x + 5, y + h - 25, w - 55, 20);
        this.updateButtons();

    }

    private void updateButtons()
    {
        this.loaded.enabled = !this.showLoaded;
        this.load.enabled = this.showLoaded;

        this.add.displayString = this.rename ? I18n.format("aperture.gui.profiles.rename") : I18n.format("aperture.gui.profiles.new");
        this.name.setTextColor(0xffffff);
    }

    /**
     * Is mouse pointer inside 
     */
    public boolean isInside(int x, int y)
    {
        return this.visible && this.rect.isInside(x, y);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (!this.visible)
        {
            return;
        }

        if (this.load.mousePressed(mc, mouseX, mouseY) || this.loaded.mousePressed(mc, mouseX, mouseY))
        {
            this.showLoaded = !this.showLoaded;
            this.updateButtons();
        }

        if (this.add.mousePressed(mc, mouseX, mouseY))
        {
            this.createCameraProfile(this.name.getText());
        }

        if (this.scrollLoaded.isInside(mouseX, mouseY))
        {
            if (this.showLoaded)
            {
                if (!this.rename)
                {
                    int index = this.scrollLoaded.getIndex(mouseX, mouseY);

                    if (index >= 0 && index < ClientProxy.control.profiles.size())
                    {
                        boolean isRename = mouseX - this.scrollLoaded.x >= this.scrollLoaded.w - 60;
                        boolean isReverse = mouseX - this.scrollLoaded.x >= this.scrollLoaded.w - 40;
                        boolean isX = mouseX - this.scrollLoaded.x >= this.scrollLoaded.w - 20;

                        if (isX)
                        {
                            /* Reset current camera profile only removed one is was current profile */
                            if (this.editor.getProfile() == ClientProxy.control.profiles.remove(index))
                            {
                                ClientProxy.control.currentProfile = null;
                                this.editor.selectProfile(null);
                            }

                            this.scrollLoaded.setSize(ClientProxy.control.profiles.size());
                        }
                        else if (isReverse)
                        {
                            CameraProfile profile = ClientProxy.control.profiles.get(index);

                            AbstractDestination dest = profile.getDestination();
                            String filename = dest.getFilename();
                            AbstractDestination newDest = dest instanceof ClientDestination ? new ServerDestination(filename) : new ClientDestination(filename);

                            if (!ClientProxy.control.hasSimilar(newDest))
                            {
                                profile.setDestination(newDest);
                            }
                        }
                        else if (isRename)
                        {
                            CameraProfile profile = ClientProxy.control.profiles.get(index);

                            this.rename = true;
                            this.updateButtons();

                            this.name.setText(profile.getDestination().getFilename());
                            this.name.setCursorPositionZero();

                            this.editor.selectProfile(ClientProxy.control.profiles.get(index));
                        }
                        else
                        {
                            this.editor.selectProfile(ClientProxy.control.profiles.get(index));
                        }
                    }
                }
            }
            else
            {
                int index = this.scrollLoad.getIndex(mouseX, mouseY);

                if (index >= 0 && index < this.destToLoad.size())
                {
                    this.destToLoad.get(index).load();
                }
            }
        }

        this.name.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void createCameraProfile(String text)
    {
        if (text.isEmpty())
        {
            return;
        }

        if (this.rename)
        {
            if (this.error)
            {
                return;
            }

            AbstractDestination dest = this.editor.getProfile().getDestination();

            dest.rename(text);
            dest.setFilename(text);

            this.rename = false;
            this.updateButtons();
        }
        else
        {
            CameraProfile profile = new CameraProfile(AbstractDestination.create(text));
            ClientProxy.control.addProfile(profile);

            this.editor.selectProfile(profile);
        }

        this.name.setText("");
        this.name.setCursorPositionZero();
    }

    @Override
    public void mouseScroll(int mouseX, int mouseY, int scroll)
    {
        ScrollArea area = this.showLoaded ? this.scrollLoaded : this.scrollLoad;

        if (area.isInside(mouseX, mouseY))
        {
            area.scrollBy(scroll);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {}

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        if (!this.visible)
        {
            return;
        }

        if (keyCode == Keyboard.KEY_RETURN)
        {
            this.createCameraProfile(this.name.getText());

            return;
        }

        this.name.textboxKeyTyped(typedChar, keyCode);

        /* Canceling renaming */
        if (this.rename)
        {
            if (keyCode == Keyboard.KEY_ESCAPE)
            {
                this.rename = false;
                this.updateButtons();
                this.name.setText("");
            }
            else
            {
                this.updateRename();
            }
        }
    }

    private void updateRename()
    {
        this.name.setTextColor(0xffffff);
        this.error = false;

        CameraProfile profile = this.editor.getProfile();

        if (profile != null)
        {
            String name = this.name.getText();
            AbstractDestination profileDest = profile.getDestination();

            for (AbstractDestination dest : this.destToLoad)
            {
                if (dest.getFilename().equals(name) && !dest.equals(profileDest))
                {
                    this.name.setTextColor(0xff2244);
                    this.error = true;

                    break;
                }
            }
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        if (!this.visible)
        {
            return;
        }

        Gui.drawRect(this.rect.x, this.rect.y, this.rect.x + this.rect.w, this.rect.y + this.rect.h, 0xaa000000);
        Gui.drawRect(this.scrollLoaded.x, this.scrollLoaded.y, this.scrollLoaded.x + this.scrollLoaded.w, this.scrollLoaded.y + this.scrollLoaded.h, 0x88000000);

        this.loaded.drawButton(mc, mouseX, mouseY);
        this.load.drawButton(mc, mouseX, mouseY);
        this.add.drawButton(mc, mouseX, mouseY);

        this.name.drawTextBox();

        if (!this.name.isFocused() && this.name.getText().isEmpty())
        {
            this.mc.fontRendererObj.drawStringWithShadow(this.rename ? I18n.format("aperture.gui.profiles.rename_profile") : I18n.format("aperture.gui.profiles.tooltip"), this.name.xPosition + 4, this.name.yPosition + 5, 0xaaaaaa);
        }

        GuiUtils.scissor(this.scrollLoaded.x, this.scrollLoaded.y, this.scrollLoaded.w, this.scrollLoaded.h, this.editor.width, this.editor.height);

        this.drawScrollArea(mouseX, mouseY);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private void drawScrollArea(int mouseX, int mouseY)
    {
        CameraControl control = ClientProxy.control;

        if (this.showLoaded)
        {
            this.scrollLoaded.setSize(control.profiles.size());

            int x = this.scrollLoaded.x;
            int y = this.scrollLoaded.y - this.scrollLoaded.scroll;
            int w = this.scrollLoaded.w;

            for (CameraProfile profile : control.profiles)
            {
                AbstractDestination dest = profile.getDestination();
                boolean hovered = this.scrollLoaded.isInside(mouseX, mouseY) && mouseY >= y && mouseY < y + this.scrollLoaded.scrollItemSize;
                boolean current = control.currentProfile != null ? dest.equals(control.currentProfile.getDestination()) : false;

                if (hovered || current)
                {
                    Gui.drawRect(x, y, x + w, y + this.scrollLoaded.scrollItemSize, current ? 0x880088ff : 0x88000000);
                }

                this.mc.fontRendererObj.drawStringWithShadow(dest.getFilename(), x + 22, y + 7, 0xffffff);
                this.mc.renderEngine.bindTexture(GuiCameraEditor.EDITOR_TEXTURE);

                if (hovered)
                {
                    boolean isX = mouseX >= x + w - 20;
                    boolean isReverse = mouseX >= x + w - 40 && mouseX < x + w - 20;
                    boolean isRename = mouseX >= x + w - 60 && mouseX < x + w - 40;

                    GlStateManager.color(1, 1, 1, 1);
                    Gui.drawModalRectWithCustomSizedTexture(x + w - 18, y + 2, 32, 32 + (isX ? 0 : 16), 16, 16, 256, 256);

                    if (dest instanceof ClientDestination)
                    {
                        Gui.drawModalRectWithCustomSizedTexture(x + w - 38, y + 2, 0, 32 + (isReverse ? 0 : 16), 16, 16, 256, 256);
                    }
                    else
                    {
                        Gui.drawModalRectWithCustomSizedTexture(x + w - 38, y + 2, 16, 32 + (isReverse ? 0 : 16), 16, 16, 256, 256);
                    }

                    Gui.drawModalRectWithCustomSizedTexture(x + w - 58, y + 2, 160, 32 + (isRename ? 0 : 16), 16, 16, 256, 256);
                }

                Gui.drawModalRectWithCustomSizedTexture(x + 2, y + 2, 0 + (dest instanceof ClientDestination ? 16 : 0), 32, 16, 16, 256, 256);

                y += this.scrollLoaded.scrollItemSize;
            }
        }
        else
        {
            int x = this.scrollLoad.x;
            int y = this.scrollLoad.y - this.scrollLoad.scroll;
            int w = this.scrollLoad.w;

            for (AbstractDestination dest : this.destToLoad)
            {
                boolean hovered = this.scrollLoad.isInside(mouseX, mouseY) && mouseY >= y && mouseY < y + this.scrollLoad.scrollItemSize;
                boolean current = control.currentProfile != null ? dest.equals(control.currentProfile.getDestination()) : false;

                if (hovered || current)
                {
                    Gui.drawRect(x, y, x + w, y + this.scrollLoaded.scrollItemSize, current ? 0x880088ff : 0x88000000);
                }

                this.mc.fontRendererObj.drawStringWithShadow(dest.getFilename(), x + 22, y + 7, 0xffffff);
                this.mc.renderEngine.bindTexture(GuiCameraEditor.EDITOR_TEXTURE);

                GlStateManager.color(1, 1, 1, 1);

                if (hovered)
                {
                    Gui.drawModalRectWithCustomSizedTexture(x + w - 18, y + 2, 48, 32, 16, 16, 256, 256);
                }

                Gui.drawModalRectWithCustomSizedTexture(x + 2, y + 2, 0 + (dest instanceof ClientDestination ? 16 : 0), 32, 16, 16, 256, 256);

                y += this.scrollLoad.scrollItemSize;
            }
        }

        ScrollArea area = this.showLoaded ? this.scrollLoaded : this.scrollLoad;

        if (area.scrollSize > area.h)
        {
            int mh = area.h - 4;
            int x = area.x + area.w - 4;
            int h = (int) (((area.scrollSize - area.h) / (float) area.h) * mh);
            int y = area.y + (int) (area.scroll / (float) (area.scrollSize - area.h) * (mh - h)) + 2;

            Gui.drawRect(x, y, x + 2, y + h, 0x88ffffff);
        }
    }

    /**
     * This dude is responsible for listening for an event of camera profile 
     * selection. 
     */
    public static interface IProfileListener
    {
        public void selectProfile(CameraProfile profile);
    }

    /**
     * Check whether this widget has any focused text fields 
     */
    public boolean hasAnyActiveTextfields()
    {
        return this.visible && this.name.isFocused();
    }

    /**
     * Rename camera profile 
     */
    public void rename(AbstractDestination from, String to)
    {
        CameraProfile profile = ClientProxy.control.getProfile(from);

        if (profile != null)
        {
            profile.getDestination().setFilename(to);
        }

        for (AbstractDestination dest : this.destToLoad)
        {
            if (dest.equals(from))
            {
                dest.setFilename(to);
            }
        }
    }
}