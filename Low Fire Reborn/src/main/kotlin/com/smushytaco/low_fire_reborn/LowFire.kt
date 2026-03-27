package com.smushytaco.low_fire_reborn
import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import net.minecraft.client.KeyMapping
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import org.lwjgl.glfw.GLFW
import kotlin.math.abs
object LowFire: ClientModInitializer {
    const val MOD_ID = "low_fire_reborn"
    val config = ModConfig.createAndLoad()
    private val KEYBIND_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "category"))
    private val TOGGLE_KEYBINDING = KeyMapping("key.$MOD_ID.toggle", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEYBIND_CATEGORY)
    private val TOGGLE_RENDER_KEYBINDING = KeyMapping("key.$MOD_ID.toggle_render", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEYBIND_CATEGORY)
    private val CYCLE_FIRE_HEIGHT_KEYBINDING = KeyMapping("key.$MOD_ID.cycle_fire_height", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEYBIND_CATEGORY)
    private val RAISE_FIRE_KEYBINDING = KeyMapping("key.$MOD_ID.raise_fire", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEYBIND_CATEGORY)
    private val LOWER_FIRE_KEYBINDING = KeyMapping("key.$MOD_ID.lower_fire", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEYBIND_CATEGORY)
    private var cycleIncrement = false
    override fun onInitializeClient() {
        KeyMappingHelper.registerKeyMapping(TOGGLE_KEYBINDING)
        KeyMappingHelper.registerKeyMapping(TOGGLE_RENDER_KEYBINDING)
        KeyMappingHelper.registerKeyMapping(CYCLE_FIRE_HEIGHT_KEYBINDING)
        KeyMappingHelper.registerKeyMapping(RAISE_FIRE_KEYBINDING)
        KeyMappingHelper.registerKeyMapping(LOWER_FIRE_KEYBINDING)
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            val player = it.player ?: return@EndTick
            while(TOGGLE_KEYBINDING.consumeClick()) {
                config.enableLowFire = !config.enableLowFire
                config.save()
                player.sendOverlayMessage(Component.literal("Low Fire is now ${if (config.enableLowFire) "on" else "off"}!"))
            }
        })
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            val player = it.player ?: return@EndTick
            while(TOGGLE_RENDER_KEYBINDING.consumeClick()) {
                config.shouldRenderFire = !config.shouldRenderFire
                config.save()
                player.sendOverlayMessage(Component.literal("Fire Rendering is now ${if (config.shouldRenderFire) "on" else "off"}!"))
            }
        })
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            val player = it.player ?: return@EndTick
            while(CYCLE_FIRE_HEIGHT_KEYBINDING.consumeClick()) {
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
                player.sendOverlayMessage(Component.literal("The fire offset has been set to ${String.format("%.2f", config.fireOffset)}!"))
            }
        })
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            val player = it.player ?: return@EndTick
            while(RAISE_FIRE_KEYBINDING.consumeClick()) {
                config.fireOffset = String.format("%.2f", config.fireOffset + abs(config.fireOffsetChange)).toDouble()
                config.save()
                player.sendOverlayMessage(Component.literal("The fire offset has been set to ${String.format("%.2f", config.fireOffset)}!"))
            }
        })
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            val player = it.player ?: return@EndTick
            while(LOWER_FIRE_KEYBINDING.consumeClick()) {
                config.fireOffset = String.format("%.2f", config.fireOffset - abs(config.fireOffsetChange)).toDouble()
                config.save()
                player.sendOverlayMessage(Component.literal("The fire offset has been set to ${String.format("%.2f", config.fireOffset)}!"))
            }
        })
    }
}