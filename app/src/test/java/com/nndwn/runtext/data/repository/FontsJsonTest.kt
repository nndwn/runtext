package com.nndwn.runtext.data.repository

import com.nndwn.runtext.data.model.FontData
import java.io.File
import kotlinx.serialization.json.Json
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FontsJsonTest {

  private lateinit var fonts: List<FontData>
  private val json = Json { ignoreUnknownKeys = true }

  @Before
  fun setUp() {
    val fontsFile = File("src/main/res/raw/fonts.json")
    assertTrue("fonts.json file should exist at src/main/res/raw/fonts.json", fontsFile.exists())

    val jsonString = fontsFile.readText()
    fonts = json.decodeFromString<List<FontData>>(jsonString)
  }

  @Test
  fun fontsJson_isParsableAndNotEmpty() {
    assertNotNull("Fonts list should not be null", fonts)
    assertTrue("Fonts list should contain at least one font", fonts.isNotEmpty())
  }

  @Test
  fun fontIds_areUnique() {
    val ids = fonts.map { it.idFont }
    val uniqueIds = ids.toSet()
    val duplicates = ids.groupBy { it }.filter { it.value.size > 1 }.keys

    assertTrue("Font IDs should be unique. Duplicates found: $duplicates", ids.size == uniqueIds.size)
  }

  @Test
  fun allFonts_haveEitherLocalResOrGoogleFont() {
    fonts.forEach { font ->
      val hasLocal = !font.localResName.isNullOrBlank()
      val hasGoogle = !font.googleFontName.isNullOrBlank()

      assertTrue(
        "Font '${font.idFont}' (${font.displayName}) must have either localResName or googleFontName",
        hasLocal || hasGoogle
      )
    }
  }

  @Test
  fun localFonts_existInResFontDirectory() {
    val resFontDir = File("src/main/res/font")
    assertTrue("src/main/res/font directory should exist", resFontDir.exists() && resFontDir.isDirectory)

    val existingFontFiles = resFontDir.listFiles()?.map { it.name } ?: emptyList()

    fonts.filter { !it.localResName.isNullOrBlank() }.forEach { font ->
      val localName = font.localResName!!
      val fileExists = existingFontFiles.any { fileName ->
        fileName == "$localName.ttf" || fileName == "$localName.otf" || fileName == "$localName.xml"
      }

      assertTrue(
        "Local font resource '$localName' for font '${font.idFont}' should exist in src/main/res/font/",
        fileExists
      )
    }
  }

  @Test
  fun googleFonts_haveNonBlankName() {
    fonts.filter { font -> font.localResName.isNullOrBlank() }.forEach { font ->
      val googleName = font.googleFontName
      assertNotNull(
        "Font '${font.idFont}' is marked as Google Font but googleFontName is null",
        googleName
      )
      assertFalse(
        "Font '${font.idFont}' has blank googleFontName",
        googleName!!.isBlank()
      )
    }
  }
}
