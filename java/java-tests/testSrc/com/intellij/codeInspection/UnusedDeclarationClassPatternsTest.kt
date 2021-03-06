/*
 * Copyright 2000-2016 JetBrains s.r.o.
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
package com.intellij.codeInspection;

import com.intellij.codeInspection.deadCode.UnusedDeclarationInspection
import com.intellij.codeInspection.ex.EntryPointsManagerBase
import com.intellij.codeInspection.ex.InspectionManagerEx
import com.intellij.codeInspection.reference.RefClass
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase

class UnusedDeclarationClassPatternsTest : LightCodeInsightFixtureTestCase() {

  fun testClassPattern() {
    val unusedDeclarationInspection = UnusedDeclarationInspection(true)
    myFixture.enableInspections(unusedDeclarationInspection)
    val classPattern = EntryPointsManagerBase.ClassPattern()
    classPattern.hierarchically = true;
    classPattern.pattern = "java.lang.Runnable"
    val patterns = EntryPointsManagerBase.getInstance(project).patterns
    try {
      patterns.add(classPattern)
      myFixture.configureByText("C.java", "public abstract class C implements Runnable {}")
      myFixture.checkHighlighting()
    }
    finally {
      patterns.remove(classPattern)
      myFixture.disableInspections(unusedDeclarationInspection)
    }
  }

  fun testNoClassPattern() {
    val unusedDeclarationInspection = UnusedDeclarationInspection(true)
    try {
      myFixture.enableInspections(unusedDeclarationInspection)
      myFixture.configureByText("C.java", "public abstract class <warning descr=\"Class 'C' is never used\">C</warning> implements Runnable {}")
      myFixture.checkHighlighting()
    }
    finally {
      myFixture.disableInspections(unusedDeclarationInspection)
    }
  }

  fun testAddEntryPoint() {
    val aClass = myFixture.addClass("public class Foo {}")
    val entryPointsManager = EntryPointsManagerBase.getInstance(project)
    val context = (InspectionManager.getInstance(project) as InspectionManagerEx).createNewGlobalContext(false)
    try {
      val refClass = context.refManager.getReference(aClass)
      assertNotNull(refClass)
      val patterns = entryPointsManager.patterns
      assertEmpty(patterns)

      //add class as entry point
      entryPointsManager.addEntryPoint(refClass!!, true)
      assertSize(1, patterns)
      assertEquals("Foo", patterns.iterator().next().pattern)
      assertEmpty(entryPointsManager.entryPoints)

      //remove class entry point with constructors - ensure nothing is left in the entries
      entryPointsManager.removeEntryPoint(refClass)
      for (constructor in (refClass as RefClass).constructors) {
        entryPointsManager.removeEntryPoint(constructor)
      }

      assertEmpty(patterns)
      assertEmpty(entryPointsManager.entryPoints)
    }
    finally {
      context.cleanup()
    }
  }
}