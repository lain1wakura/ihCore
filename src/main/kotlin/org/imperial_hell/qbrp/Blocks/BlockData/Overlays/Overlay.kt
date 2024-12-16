package org.imperial_hell.qbrp.Blocks.BlockData.Overlays

import net.minecraft.util.Identifier
import org.imperial_hell.qbrp.Blocks.BlockData.BlockData

data class Overlay(val texture: Identifier,
                   val side: String,
                   var scale: Double = 1.0,
                   var rotation: Double = 0.0,
                   var opacity: Double = 1.0,
                   var offsetX: Double = 0.0,
                   var offsetY: Double = 0.0) : BlockData