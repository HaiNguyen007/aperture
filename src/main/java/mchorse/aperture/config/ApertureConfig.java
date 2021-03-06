package mchorse.aperture.config;

import mchorse.aperture.Aperture;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Aperture config class
 *
 * This class stores config properties powered by Forge built-in configuration
 * library.
 *
 * I looked up how this configuration works in Minema mod, so don't wonder if
 * it looks pretty similar. I also looked up Chroonster TestMod3.
 */
public class ApertureConfig
{
    /**
     * Camera duration step (used by keyboard duration bindings)
     */
    public int camera_duration_step;

    /**
     * Default camera duration (used by keyboard fixture bindings)
     */
    public int camera_duration;

    /**
     * Interpolate target fixtures position?
     */
    public boolean camera_interpolate_target;

    /**
     * Ratio for target fixtures interpolation
     */
    public float camera_interpolate_target_value;

    /**
     * Switch to spectator mode when starting camera playback
     */
    public boolean camera_spectator;

    /**
     * Factor for step keys
     */
    public float camera_step_factor;

    /**
     * Factor for rotate keys
     */
    public float camera_rotate_factor;

    /**
     * Activate Minema recording on camera start and deactivate on camera stop
     */
    public boolean camera_minema;

    /**
     * Clamp smooth camera's pitch between -90 and 90 degrees range?
     */
    public boolean camera_smooth_clamp;

    /**
     * Default camera path interpolation method
     */
    public String camera_path_default_interp;

    /**
     * Allows you to rebind /camera command's name (if you want to type less)
     */
    public String camera_command_name;

    /**
     * Simulate player's velocity during camera playback
     */
    public boolean camera_simulate_velocity;

    /**
     * Camera debug ticks
     */
    public boolean camera_debug_ticks;

    /**
     * First camera tick must be zero
     */
    public boolean camera_first_tick_zero;

    /**
     * Render camera profile in the world?
     */
    public boolean camera_profile_render;

    /**
     * Render a custom mouse pointer in GUIs
     */
    public boolean gui_render_mouse;

    /**
     * Use "/minecraft:tp" in multiplayer's playback?
     */
    public boolean minecrafttp_teleport;

    /**
     * Use "/tp" in multiplayer's playback?
     */
    public boolean tp_teleport;

    /**
     * Whether camera should be played back from outside (allowing the 
     * player to act as a body actor)
     */
    public boolean camera_outside;

    /**
     * Whether player should be hidden during playback of the camera 
     * if outside mode is enabled
     */
    public boolean camera_outside_hide_player;

    /**
     * If enabled, it fixes the sky rendering, but hides the block 
     * highlight
     */
    public boolean camera_outside_sky;

    /* Non conifg option stuff */

    /**
     * Forge configuration
     */
    public Configuration config;

    public ApertureConfig(Configuration config)
    {
        this.config = config;
        this.reload();
    }

    /**
     * Reload config values
     */
    public void reload()
    {
        String camera = "camera";
        String outside = "outside";
        String prefix = "aperture.config.camera.";

        /* Camera */
        this.camera_duration_step = this.config.getInt("camera_duration_step", camera, 10, 1, 100, "What is default step to use when adding or reducing duration of the camera fixture (in ticks)", prefix + "camera_duration_step");
        this.camera_duration = this.config.getInt("camera_duration", camera, 30, 1, 1000, "What is default duration of the camera fixture (in ticks)", prefix + "camera_duration");
        this.camera_interpolate_target = this.config.getBoolean("camera_interpolate_target", camera, false, "Interpolate target based camera fixtures (follow and look) outcome", prefix + "camera_interpolate_target");
        this.camera_interpolate_target_value = this.config.getFloat("camera_interpolate_target_value", camera, 0.5F, 0.0F, 1.0F, "Interpolation value for target based camera fixture interpolation", prefix + "camera_interpolate_target_value");
        this.camera_spectator = this.config.getBoolean("camera_spectator", camera, true, "Switch to spectator mode when starting camera playback", prefix + "camera_spectator");
        this.camera_step_factor = this.config.getFloat("camera_step_factor", camera, 0.01F, 0, 10, "Camera step factor for step keys", prefix + "camera_step_factor");
        this.camera_rotate_factor = this.config.getFloat("camera_rotate_factor", camera, 0.1F, 0, 10, "Camera rotate factor for rotate keys", prefix + "camera_rotate_factor");
        this.camera_minema = this.config.getBoolean("camera_minema", camera, false, "Activate Minema recording on camera start and deactivate on camera stop", prefix + "camera_minema");
        this.camera_path_default_interp = this.config.getString("camera_path_default_interp", camera, "linear", "Default interpolation method for path fixture", prefix + "camera_path_default_interp");
        this.camera_command_name = this.config.getString("camera_command_name", camera, "camera", "Allows you to rebind camera command's name (requires game reload to take effect)", prefix + "camera_command_name");
        this.camera_simulate_velocity = this.config.getBoolean("camera_simulate_velocity", camera, false, "Simulate player's velocity during camera playback (see legs in perspective)", prefix + "camera_simulate_velocity");
        this.camera_debug_ticks = this.config.getBoolean("camera_debug_ticks", camera, false, "Write ticks to the log during camera playback", prefix + "camera_debug_ticks");
        this.camera_first_tick_zero = this.config.getBoolean("camera_first_tick_zero", camera, false, "When camera runner starts, start the actual playback when partial tick is exactly zero", prefix + "camera_first_tick_zero");
        this.camera_profile_render = this.config.getBoolean("camera_profile_render", camera, true, "Render camera profile in the world?", prefix + "camera_profile_render");
        this.minecrafttp_teleport = this.config.getBoolean("minecrafttp_teleport", camera, true, "When start the camera playback in multiplayer, teleport you with /minecraft:tp command (For Essentials)", prefix + "minecrafttp_teleport");
        this.tp_teleport = this.config.getBoolean("tp_teleport", camera, true, "When start the camera playback in multiplayer, teleport you with /tp command (For Vanilla or Forge)", prefix + "tp_teleport");

        /* Camera outside mode */
        this.camera_outside = this.config.getBoolean("camera_outside", outside, false, "Whether camera should be played back from outside (allowing the player to act as a body actor)", prefix + "camera_outside");
        this.camera_outside_hide_player = this.config.getBoolean("camera_outside_hide_player", outside, false, "Whether player should be hidden during playback of the camera if outside mode is enabled", prefix + "camera_outside_hide_player");
        this.camera_outside_sky = this.config.getBoolean("camera_outside_sky", outside, true, "If enabled, it fixes the sky rendering, but hides the block highlight", prefix + "camera_outside_sky");

        /* Processing camera command name */
        this.camera_command_name = this.camera_command_name.trim().replaceAll("[^\\w\\d_\\-]+", "");

        /* Mouse rendering */
        this.gui_render_mouse = this.config.getBoolean("gui_render_mouse", camera, false, "Render a custom mouse pointer in GUIs", prefix + "gui_render_mouse");

        if (this.camera_command_name.isEmpty())
        {
            this.camera_command_name = "camera";
        }

        /* Smooth camera */
        this.camera_smooth_clamp = this.config.getBoolean("camera_smooth_clamp", "smooth", true, "Clip smooth camera's pitch between -90 and 90 degrees range?", "aperture.config.smooth.camera_smooth_clamp");

        Aperture.proxy.onConfigChange(this.config);

        if (this.config.hasChanged())
        {
            this.config.save();
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(Aperture.MODID) && this.config.hasChanged())
        {
            this.reload();
        }
    }
}
