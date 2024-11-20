package com.smushytaco.low_fire_reborn
import com.smushytaco.low_fire_reborn.configuration_support.ModConfiguration
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.ConfigHolder
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import kotlin.math.abs
object LowFire: ClientModInitializer {
    const val MOD_ID = "low_fire_reborn"
    private lateinit var configHolder: ConfigHolder<ModConfiguration>
    lateinit var config: ModConfiguration
        private set
    private val TOGGLE_KEYBINDING = KeyBinding("key.$MOD_ID.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.$MOD_ID.$MOD_ID")
    private val TOGGLE_RENDER_KEYBINDING = KeyBinding("key.$MOD_ID.toggle_render", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.$MOD_ID.$MOD_ID")
    private val CYCLE_FIRE_HEIGHT_KEYBINDING = KeyBinding("key.$MOD_ID.cycle_fire_height", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.$MOD_ID.$MOD_ID")
    private val RAISE_FIRE_KEYBINDING = KeyBinding("key.$MOD_ID.raise_fire", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.$MOD_ID.$MOD_ID")
    private val LOWER_FIRE_KEYBINDING = KeyBinding("key.$MOD_ID.lower_fire", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.$MOD_ID.$MOD_ID")
    private var cycleIncrement = false
    override fun onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(TOGGLE_KEYBINDING)
        KeyBindingHelper.registerKeyBinding(TOGGLE_RENDER_KEYBINDING)
        KeyBindingHelper.registerKeyBinding(CYCLE_FIRE_HEIGHT_KEYBINDING)
        KeyBindingHelper.registerKeyBinding(RAISE_FIRE_KEYBINDING)
        KeyBindingHelper.registerKeyBinding(LOWER_FIRE_KEYBINDING)
        AutoConfig.register(ModConfiguration::class.java) { definition: Config, configClass: Class<ModConfiguration> ->
            GsonConfigSerializer(definition, configClass)
        }
        configHolder = AutoConfig.getConfigHolder(ModConfiguration::class.java)
        config = configHolder.config
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            val player = it.player ?: return@EndTick
            while(TOGGLE_KEYBINDING.wasPressed()) {
                config.enableLowFire = !config.enableLowFire
                configHolder.save()
                player.sendMessage(Text.of("Low Fire is now ${if (config.enableLowFire) "on" else "off"}!"), true)
            }
        })
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            val player = it.player ?: return@EndTick
            while(TOGGLE_RENDER_KEYBINDING.wasPressed()) {
                config.shouldRenderFire = !config.shouldRenderFire
                configHolder.save()
                player.sendMessage(Text.of("Fire Rendering is now ${if (config.shouldRenderFire) "on" else "off"}!"), true)
            }
        })
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            val player = it.player ?: return@EndTick
            while(CYCLE_FIRE_HEIGHT_KEYBINDING.wasPressed()) {
                if (config.fireOffsetCycleUpperKeyLimit < config.fireOffsetCycleLowerKeyLimit) {
                    val temp = config.fireOffsetCycleUpperKeyLimit
                    config.fireOffsetCycleUpperKeyLimit = config.fireOffsetCycleLowerKeyLimit
                    config.fireOffsetCycleLowerKeyLimit = temp
                } else if (config.fireOffsetCycleUpperKeyLimit == config.fireOffsetCycleLowerKeyLimit) {
                    config.fireOffsetCycleUpperKeyLimit += config.fireOffsetChange
                }
                val nextOffset = if (cycleIncrement) {
                    String.format("%.2f", config.fireOffset + abs(config.fireOffsetChange)).toDouble()
                } else {
                    String.format("%.2f", config.fireOffset - abs(config.fireOffsetChange)).toDouble()
                }
                if (nextOffset in config.fireOffsetCycleLowerKeyLimit..config.fireOffsetCycleUpperKeyLimit) {
                    config.fireOffset = nextOffset
                } else {
                    cycleIncrement = config.fireOffset < config.fireOffsetCycleUpperKeyLimit
                    if (cycleIncrement) {
                        config.fireOffset = String.format("%.2f", config.fireOffset + abs(config.fireOffsetChange)).toDouble()
                    } else {
                        config.fireOffset = String.format("%.2f", config.fireOffset - abs(config.fireOffsetChange)).toDouble()
                    }
                }
                configHolder.save()
                player.sendMessage(Text.of("The fire offset has been set to ${String.format("%.2f", config.fireOffset)}!"), true)
            }
        })
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            val player = it.player ?: return@EndTick
            while(RAISE_FIRE_KEYBINDING.wasPressed()) {
                config.fireOffset = String.format("%.2f", config.fireOffset + abs(config.fireOffsetChange)).toDouble()
                configHolder.save()
                player.sendMessage(Text.of("The fire offset has been set to ${String.format("%.2f", config.fireOffset)}!"), true)
            }
        })
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            val player = it.player ?: return@EndTick
            while(LOWER_FIRE_KEYBINDING.wasPressed()) {
                config.fireOffset = String.format("%.2f", config.fireOffset - abs(config.fireOffsetChange)).toDouble()
                configHolder.save()
                player.sendMessage(Text.of("The fire offset has been set to ${String.format("%.2f", config.fireOffset)}!"), true)
            }
        })
    }
}