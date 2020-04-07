package com.github.cheapmon.balalaika.data.config

import com.github.cheapmon.balalaika.data.resources.ResourceLoader
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import javax.inject.Inject

class YamlConfigLoader @Inject constructor(
    private val res: ResourceLoader
) : ConfigLoader {
    override fun readConfig(): Config {
        val yaml = Yaml(Constructor(Config::class.java))
        return yaml.load(res.read(res.configId))
    }
}