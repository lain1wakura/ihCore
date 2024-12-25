package org.imperial_hell.qbrp.Resources.Structure

import java.nio.file.Path
import java.util.UUID

class UContainer(
    path: Path,
    val uuid: String = UUID.randomUUID().toString() ) : Branch(path.resolve(uuid)) {
}