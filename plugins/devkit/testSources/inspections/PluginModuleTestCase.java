/*
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.idea.devkit.inspections;

import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.TestDataFile;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.devkit.build.PluginBuildConfiguration;
import org.jetbrains.idea.devkit.module.PluginModuleType;

public abstract class PluginModuleTestCase extends LightCodeInsightFixtureTestCase {
  @NotNull
  @Override
  protected LightProjectDescriptor getProjectDescriptor() {
    return new DefaultLightProjectDescriptor() {
      @NotNull
      @Override
      public ModuleType getModuleType() {
        return PluginModuleType.getInstance();
      }
    };
  }

  protected void setPluginXml(@TestDataFile String pluginXml) {
    final VirtualFile file = myFixture.copyFileToProject(pluginXml, "META-INF/plugin.xml");
    final PluginBuildConfiguration pluginBuildConfiguration = PluginBuildConfiguration.getInstance(myModule);
    assertNotNull(pluginBuildConfiguration);
    pluginBuildConfiguration.setPluginXmlFromVirtualFile(file);
  }
}
