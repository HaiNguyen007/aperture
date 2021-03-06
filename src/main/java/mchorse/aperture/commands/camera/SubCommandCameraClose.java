package mchorse.aperture.commands.camera;

import java.util.Iterator;

import mchorse.aperture.ClientProxy;
import mchorse.aperture.camera.CameraControl;
import mchorse.aperture.camera.CameraProfile;
import mchorse.aperture.camera.destination.AbstractDestination;
import mchorse.aperture.camera.destination.ServerDestination;
import mchorse.aperture.utils.L10n;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

/**
 * Camera's sub-command /camera close
 *
 * This sub-command is responsible for closing a camera profile from the current 
 * list of camera profiles. 
 */
public class SubCommandCameraClose extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "close";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "aperture.commands.camera.close";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        CameraControl control = ClientProxy.control;

        String filename = args[1];
        boolean isServer = args[0].equals("server");

        Iterator<CameraProfile> it = control.profiles.iterator();

        while (it.hasNext())
        {
            CameraProfile profile = it.next();
            AbstractDestination dest = profile.getDestination();

            if (dest.getFilename().equals(filename) && isServer == dest instanceof ServerDestination)
            {
                it.remove();

                if (control.currentProfile == profile)
                {
                    control.currentProfile = null;
                }

                break;
            }
        }

        L10n.info(sender, "profile.closed", filename, I18n.format("aperture.misc." + (isServer ? "server" : "client")));
    }
}