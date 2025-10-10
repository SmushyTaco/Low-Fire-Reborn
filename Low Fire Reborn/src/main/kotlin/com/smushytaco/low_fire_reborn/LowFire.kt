package com.smushytaco.low_fire_reborn
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW
import kotlin.math.abs
object LowFire: ClientModInitializer {
    const val MOD_ID = "low_fire_reborn"
    val config = ModConfig.createAndLoad()
    private val KEYBIND_CATEGORY = KeyBinding.Category.create(Identifier.of(MOD_ID, "category"))
    private val TOGGLE_KEYBINDING = KeyBinding("key.$MOD_ID.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEYBIND_CATEGORY)
    private val TOGGLE_RENDER_KEYBINDING = KeyBinding("key.$MOD_ID.toggle_render", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEYBIND_CATEGORY)
    private val CYCLE_FIRE_HEIGHT_KEYBINDING = KeyBinding("key.$MOD_ID.cycle_fire_height", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEYBIND_CATEGORY)
    private val RAISE_FIRE_KEYBINDING = KeyBinding("key.$MOD_ID.raise_fire", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEYBIND_CATEGORY)
    private val LOWER_FIRE_KEYBINDING = KeyBinding("key.$MOD_ID.lower_fire", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEYBIND_CATEGORY)
    private var cycleIncrement = false
    override fun onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(TOGGLE_KEYBINDING)
        KeyBindingHelper.registerKeyBinding(TOGGLE_RENDER_KEYBINDING)
        KeyBindingHelper.registerKeyBinding(CYCLE_FIRE_HEIGHT_KEYBINDING)
        KeyBindingHelper.registerKeyBinding(RAISE_FIRE_KEYBINDING)
        KeyBindingHelper.registerKeyBinding(LOWER_FIRE_KEYBINDING)
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            val player = it.player ?: return@EndTick
            while(TOGGLE_KEYBINDING.wasPressed()) {
                config.enableLowFire = !config.enableLowFire
                config.save()
                player.sendMessage(Text.of("Low Fire is now ${if (config.enableLowFire) "on" else "off"}!"), true)
            }
        })
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            val player = it.player ?: return@EndTick
            while(TOGGLE_RENDER_KEYBINDING.wasPressed()) {
                config.shouldRenderFire = !config.shouldRenderFire
                config.save()
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
                config.save()
                player.sendMessage(Text.of("The fire offset has been set to ${String.format("%.2f", config.fireOffset)}!"), true)
            }
        })
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            val player = it.player ?: return@EndTick
            while(RAISE_FIRE_KEYBINDING.wasPressed()) {
                config.fireOffset = String.format("%.2f", config.fireOffset + abs(config.fireOffsetChange)).toDouble()
                config.save()
                player.sendMessage(Text.of("The fire offset has been set to ${String.format("%.2f", config.fireOffset)}!"), true)
            }
        })
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            val player = it.player ?: return@EndTick
            while(LOWER_FIRE_KEYBINDING.wasPressed()) {
                config.fireOffset = String.format("%.2f", config.fireOffset - abs(config.fireOffsetChange)).toDouble()
                config.save()
                player.sendMessage(Text.of("The fire offset has been set to ${String.format("%.2f", config.fireOffset)}!"), true)
            }
        })
    }
}