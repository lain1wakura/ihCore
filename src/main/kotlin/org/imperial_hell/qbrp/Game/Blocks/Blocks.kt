package org.imperial_hell.qbrp.Game.Blocks

import org.imperial_hell.qbrp.System.Files.DatabaseManager

class Blocks(
    val database: DatabaseManager) {
    val dbService = qbBlocksService(database).apply { database.connect() }
    val manager = BlockDataManager(dbService)
}