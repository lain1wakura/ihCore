package org.imperial_hell.qbrp.Blocks

import net.minecraft.util.math.BlockPos
import org.imperial_hell.qbrp.Blocks.BlockData.BlockData
import java.util.UUID

data class qbBlock(var uuid: String = UUID.randomUUID().toString(),
                   var blockData: List<BlockData> = listOf()) {

}