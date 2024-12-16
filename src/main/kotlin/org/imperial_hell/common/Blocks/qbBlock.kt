package org.imperial_hell.common.Blocks

import org.imperial_hell.common.Blocks.BlockData.BlockData
import java.util.UUID

data class qbBlock(var uuid: String = UUID.randomUUID().toString(),
                   var blockData: List<BlockData> = listOf()) {

}