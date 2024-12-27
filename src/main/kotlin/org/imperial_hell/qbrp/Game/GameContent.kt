package org.imperial_hell.qbrp.Game

import org.imperial_hell.qbrp.Game.Blocks.BlockDataManager
import org.imperial_hell.qbrp.Game.Blocks.Blocks
import org.imperial_hell.qbrp.Game.Blocks.qbBlocksService
import org.imperial_hell.qbrp.Game.Items.Items
import org.imperial_hell.qbrp.Secrets.Databases
import org.imperial_hell.qbrp.System.Files.DatabaseManager
import org.imperial_hell.qbrp.System.Files.IhConfig

class GameContent() {
    val items = Items(IhConfig.SERVER_ITEM.toFile())
    val blocks = Blocks(DatabaseManager(Databases.BLOCKS_KEY, "qbblocks"))
}
