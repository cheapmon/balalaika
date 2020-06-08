/*
 * Copyright 2020 Simon Kaleschke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.cheapmon.balalaika.data.config

import com.github.cheapmon.balalaika.data.resources.ResourceLoader
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import javax.inject.Inject

/**
 * [ConfigLoader] which creates a configuration object from a YAML source file
 *
 * The source file is read by the [ResourceLoader] and deserialized using SnakeYAML.
 * An example configuration source file might look like this:
 * ```yaml
 * version: 1
 * sources:
 *   - name: "Source name"
 *     authors: "List of authors"
 *     summary: "Short description of source"
 *     url: "example.org/source"
 * ```
 *
 * @see Config
 * @see ConfigLoader
 */
class YamlConfigLoader @Inject constructor(
    /** Input file loader */
    private val res: ResourceLoader
) : ConfigLoader {
    /** Read YAML source file and deserialize into configuration object */
    override fun readConfig(): Config {
        val yaml = Yaml(Constructor(Config::class.java))
        return yaml.load(res.read(res.configId))
    }
}