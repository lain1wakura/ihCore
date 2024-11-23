package org.imperial_hell.ihcore.Files

import java.nio.file.Paths

object IhConfig {

    val configFilePath: java.nio.file.Path = Paths.get("config/ihCore.conf")

    //val absolutePath: String = "C:\\Users\\user\\Desktop\\imperialhell\\Bot"
    val absolutePath: String = "/home/container/imperial-hell"

}