package com.example.whitenoises

import org.junit.Test
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

class SoundGeneratorTest {

    @Test
    fun generatePureVoiceFreeSounds() {
        val rawDir = File("src/main/res/raw").takeIf { it.exists() } 
            ?: File("app/src/main/res/raw").also { it.mkdirs() }

        // Color Noises
        generateColorNoise(File(rawDir, "white_noise.wav"), NoiseType.WHITE)
        generateColorNoise(File(rawDir, "pink_noise.wav"), NoiseType.PINK)
        generateColorNoise(File(rawDir, "brown_noise.wav"), NoiseType.BROWN)

        // Fans
        generateBoxFan(File(rawDir, "box_fan.wav"))
        generateCeilingFan(File(rawDir, "fan.wav"))
        generateOscillatingFan(File(rawDir, "oscillating_fan.wav"))

        // Rain & Weather
        generateRain(File(rawDir, "rain.wav"))
        generateRainRoof(File(rawDir, "rain_roof.wav"))
        generateRainTent(File(rawDir, "rain_tent.wav"))
        generateThunder(File(rawDir, "thunder.wav"))

        // Baby & Sleep
        generateWomb(File(rawDir, "womb.wav"))
        generateShusher(File(rawDir, "shusher.wav"))
        generateHeartbeat(File(rawDir, "heartbeat.wav"))
        generateHairdryer(File(rawDir, "hairdryer.wav"))
        generateVacuum(File(rawDir, "vacuum.wav"))
        generateLullaby(File(rawDir, "lullaby.wav"))

        // Nature & Water
        generateOcean(File(rawDir, "ocean.wav"))
        generateStream(File(rawDir, "stream.wav"))
        generateCampfire(File(rawDir, "campfire.wav"))
        generateCrickets(File(rawDir, "crickets.wav"))
        generateWind(File(rawDir, "wind.wav"))

        // Household & Travel
        generateAirConditioner(File(rawDir, "ac.wav"))
        generateTrain(File(rawDir, "train.wav"))
        generateAirplane(File(rawDir, "airplane.wav"))
        generateClock(File(rawDir, "clock.wav"))
    }

    private enum class NoiseType { WHITE, PINK, BROWN }

    private fun writeWavHeader(fos: FileOutputStream, sampleRate: Int, numChannels: Int, totalSamples: Int) {
        val dataSize = totalSamples * numChannels * 2
        val fileSize = 36 + dataSize
        
        val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN).apply {
            put("RIFF".toByteArray())
            putInt(fileSize)
            put("WAVE".toByteArray())
            put("fmt ".toByteArray())
            putInt(16)
            putShort(1.toShort())
            putShort(numChannels.toShort())
            putInt(sampleRate)
            putInt(sampleRate * numChannels * 2)
            putShort((numChannels * 2).toShort())
            putShort(16.toShort())
            put("data".toByteArray())
            putInt(dataSize)
        }
        fos.write(header.array())
    }

    private fun writeWavFile(file: File, sampleRate: Int, samples: ShortArray) {
        FileOutputStream(file).use { fos ->
            writeWavHeader(fos, sampleRate, 1, samples.size)
            val buf = ByteBuffer.allocate(samples.size * 2).order(ByteOrder.LITTLE_ENDIAN)
            for (s in samples) buf.putShort(s)
            fos.write(buf.array())
        }
    }

    private fun generateColorNoise(file: File, type: NoiseType) {
        val sampleRate = 44100
        val totalSamples = sampleRate * 8
        val samples = ShortArray(totalSamples)

        var b0 = 0.0; var b1 = 0.0; var b2 = 0.0; var brown = 0.0

        for (i in 0 until totalSamples) {
            val white = Random.nextDouble(-1.0, 1.0)
            val sample = when (type) {
                NoiseType.WHITE -> white * 16000.0
                NoiseType.PINK -> {
                    b0 = 0.99886 * b0 + white * 0.0555179
                    b1 = 0.99332 * b1 + white * 0.0750759
                    b2 = 0.96900 * b2 + white * 0.1538520
                    (b0 + b1 + b2 + white * 0.5362) * 2500.0
                }
                NoiseType.BROWN -> {
                    brown = (brown + (0.02 * white)) / 1.02
                    brown * 16000.0
                }
            }
            samples[i] = sample.toInt().coerceIn(-32768, 32767).toShort()
        }
        writeWavFile(file, sampleRate, samples)
    }

    private fun generateBoxFan(file: File) {
        val sampleRate = 44100
        val totalSamples = sampleRate * 6
        val samples = ShortArray(totalSamples)
        var pink = 0.0
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val white = Random.nextDouble(-1.0, 1.0)
            pink = 0.95 * pink + 0.05 * white
            val hum = sin(2.0 * PI * 80.0 * t) * 3500.0
            val sample = pink * 15000.0 + hum
            samples[i] = sample.toInt().coerceIn(-32768, 32767).toShort()
        }
        writeWavFile(file, sampleRate, samples)
    }

    private fun generateCeilingFan(file: File) {
        val sampleRate = 44100
        val totalSamples = sampleRate * 6
        val samples = ShortArray(totalSamples)
        var pink = 0.0
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val white = Random.nextDouble(-1.0, 1.0)
            pink = 0.97 * pink + 0.03 * white
            val bladeSweep = 0.85 + 0.15 * sin(2.0 * PI * 28.0 * t)
            val hum = sin(2.0 * PI * 55.0 * t) * 2000.0
            val sample = pink * 14000.0 * bladeSweep + hum
            samples[i] = sample.toInt().coerceIn(-32768, 32767).toShort()
        }
        writeWavFile(file, sampleRate, samples)
    }

    private fun generateOscillatingFan(file: File) {
        val sampleRate = 44100
        val totalSamples = sampleRate * 8
        val samples = ShortArray(totalSamples)
        var pink = 0.0
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val white = Random.nextDouble(-1.0, 1.0)
            pink = 0.96 * pink + 0.04 * white
            val oscSweep = 0.6 + 0.4 * sin(2.0 * PI * t / 4.0)
            val sample = pink * 16000.0 * oscSweep
            samples[i] = sample.toInt().coerceIn(-32768, 32767).toShort()
        }
        writeWavFile(file, sampleRate, samples)
    }

    private fun generateRain(file: File) {
        val sampleRate = 44100
        val totalSamples = sampleRate * 8
        val samples = ShortArray(totalSamples)
        var b0 = 0.0; var b1 = 0.0; var b2 = 0.0; var lp = 0.0

        for (i in 0 until totalSamples) {
            val white = Random.nextDouble(-1.0, 1.0)
            b0 = 0.99886 * b0 + white * 0.0555179
            b1 = 0.99332 * b1 + white * 0.0750759
            b2 = 0.96900 * b2 + white * 0.1538520
            val pink = b0 + b1 + b2 + white * 0.5362
            lp = 0.85 * lp + 0.15 * pink
            var sample = lp * 3500.0

            if (Random.nextDouble() < 0.015) {
                val dropFreq = Random.nextDouble(1800.0, 4200.0)
                val dropLen = (sampleRate * Random.nextDouble(0.003, 0.012)).toInt()
                for (j in 0 until dropLen) {
                    if (i + j < totalSamples) {
                        val t = j.toDouble() / sampleRate
                        val env = exp(-t * 350.0)
                        val dropVal = sin(2.0 * PI * dropFreq * t) * env * 12000.0
                        samples[i + j] = (samples[i + j] + dropVal.toInt()).coerceIn(-32768, 32767).toShort()
                    }
                }
            }

            samples[i] = (samples[i] + sample.toInt()).coerceIn(-32768, 32767).toShort()
        }
        writeWavFile(file, sampleRate, samples)
    }

    private fun generateRainRoof(file: File) {
        val sampleRate = 44100
        val totalSamples = sampleRate * 8
        val samples = ShortArray(totalSamples)
        var brown = 0.0
        for (i in 0 until totalSamples) {
            val white = Random.nextDouble(-1.0, 1.0)
            brown = (brown + (0.03 * white)) / 1.03
            var sample = brown * 14000.0
            if (Random.nextDouble() < 0.02) {
                val p = (i % 150).toDouble() / sampleRate
                sample += sin(2.0 * PI * 1200.0 * p) * 12000.0 * exp(-p * 250.0)
            }
            samples[i] = sample.toInt().coerceIn(-32768, 32767).toShort()
        }
        writeWavFile(file, sampleRate, samples)
    }

    private fun generateRainTent(file: File) {
        val sampleRate = 44100
        val totalSamples = sampleRate * 8
        val samples = ShortArray(totalSamples)
        var pink = 0.0
        for (i in 0 until totalSamples) {
            val white = Random.nextDouble(-1.0, 1.0)
            pink = 0.9 * pink + 0.1 * white
            var sample = pink * 12000.0
            if (Random.nextDouble() < 0.025) {
                val p = (i % 120).toDouble() / sampleRate
                sample += sin(2.0 * PI * 2800.0 * p) * 11000.0 * exp(-p * 400.0)
            }
            samples[i] = sample.toInt().coerceIn(-32768, 32767).toShort()
        }
        writeWavFile(file, sampleRate, samples)
    }

    private fun generateThunder(file: File) {
        val sampleRate = 44100
        val totalSamples = sampleRate * 10
        val samples = ShortArray(totalSamples)
        var brown = 0.0
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val white = Random.nextDouble(-1.0, 1.0)
            brown = (brown + (0.015 * white)) / 1.015
            val thunderRumble = if (t in 2.0..6.0) exp(-(t - 2.0) * 0.8) * sin(2.0 * PI * 30.0 * t) * 22000.0 else 0.0
            val sample = brown * 8000.0 + thunderRumble
            samples[i] = sample.toInt().coerceIn(-32768, 32767).toShort()
        }
        writeWavFile(file, sampleRate, samples)
    }

    private fun generateWomb(file: File) {
        val sampleRate = 44100
        val totalSamples = sampleRate * 8
        val samples = ShortArray(totalSamples)
        
        var brown = 0.0
        val interval = (sampleRate * 60) / 72 // 72 BPM maternal pulse

        for (i in 0 until totalSamples) {
            val white = Random.nextDouble(-1.0, 1.0)
            
            // Soft background fluid bed
            brown = (brown + (0.01 * white)) / 1.01
            var sample = brown * 5000.0

            val pos = i % interval
            val pulsePhase = pos.toDouble() / interval

            // Subtle fluid swish
            val swishEnv = sin(PI * pulsePhase)
            val swishNoise = Random.nextDouble(-1.0, 1.0) * 1500.0 * (swishEnv * swishEnv)
            sample += swishNoise

            // Prominent maternal heartbeat thump (S1 - lub)
            if (pos < sampleRate * 0.12) {
                val bt = pos.toDouble() / sampleRate
                val lub = (sin(2.0 * PI * 42.0 * bt) + 0.6 * sin(2.0 * PI * 21.0 * bt)) * exp(-bt * 20.0) * 28000.0
                sample += lub
            }
            // Prominent maternal heartbeat thump (S2 - dub)
            val dubStart = (sampleRate * 0.16).toInt()
            if (pos >= dubStart && pos < dubStart + sampleRate * 0.10) {
                val bt = (pos - dubStart).toDouble() / sampleRate
                val dub = sin(2.0 * PI * 60.0 * bt) * exp(-bt * 25.0) * 22000.0
                sample += dub
            }

            samples[i] = sample.toInt().coerceIn(-32768, 32767).toShort()
        }
        writeWavFile(file, sampleRate, samples)
    }

    private fun generateShusher(file: File) {
        val sampleRate = 44100
        val totalSamples = sampleRate * 6
        val samples = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val shushCycle = sin(2.0 * PI * t / 2.0)
            val env = if (shushCycle > 0) shushCycle else 0.0
            val white = Random.nextDouble(-1.0, 1.0)
            val sample = white * 18000.0 * env
            samples[i] = sample.toInt().coerceIn(-32768, 32767).toShort()
        }
        writeWavFile(file, sampleRate, samples)
    }

    private fun generateHeartbeat(file: File) {
        val sampleRate = 44100
        val totalSamples = sampleRate * 6
        val samples = ShortArray(totalSamples)
        val bpm = 70
        val interval = (sampleRate * 60) / bpm
        for (i in 0 until totalSamples) {
            val pos = i % interval
            if (pos < sampleRate * 0.12) {
                val t = pos.toDouble() / sampleRate
                val lub = (sin(2.0 * PI * 50.0 * t) + 0.5 * sin(2.0 * PI * 25.0 * t)) * exp(-t * 22.0) * 26000.0
                samples[i] = lub.toInt().coerceIn(-32768, 32767).toShort()
            }
            val dubStart = (sampleRate * 0.18).toInt()
            if (pos >= dubStart && pos < dubStart + sampleRate * 0.08) {
                val t = (pos - dubStart).toDouble() / sampleRate
                val dub = sin(2.0 * PI * 75.0 * t) * exp(-t * 30.0) * 20000.0
                samples[i] = dub.toInt().coerceIn(-32768, 32767).toShort()
            }
        }
        writeWavFile(file, sampleRate, samples)
    }

    private fun generateHairdryer(file: File) {
        val sampleRate = 44100
        val totalSamples = sampleRate * 6
        val samples = ShortArray(totalSamples)
        var pink = 0.0
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val white = Random.nextDouble(-1.0, 1.0)
            pink = 0.93 * pink + 0.07 * white
            val motor = sin(2.0 * PI * 120.0 * t) * 4000.0 + sin(2.0 * PI * 240.0 * t) * 2000.0
            val sample = pink * 14000.0 + motor
            samples[i] = sample.toInt().coerceIn(-32768, 32767).toShort()
        }
        writeWavFile(file, sampleRate, samples)
    }

    private fun generateVacuum(file: File) {
        val sampleRate = 44100
        val totalSamples = sampleRate * 6
        val samples = ShortArray(totalSamples)
        var brown = 0.0
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val white = Random.nextDouble(-1.0, 1.0)
            brown = (brown + (0.04 * white)) / 1.04
            val motor = sin(2.0 * PI * 85.0 * t) * 9000.0 + sin(2.0 * PI * 170.0 * t) * 4500.0
            val sample = brown * 12000.0 + motor
            samples[i] = sample.toInt().coerceIn(-32768, 32767).toShort()
        }
        writeWavFile(file, sampleRate, samples)
    }

    private fun generateLullaby(file: File) {
        val sampleRate = 44100
        val totalSamples = sampleRate * 8
        val samples = ShortArray(totalSamples)
        val notes = doubleArrayOf(261.63, 329.63, 392.00, 523.25, 392.00, 329.63)
        val noteLen = totalSamples / notes.size
        for (i in 0 until totalSamples) {
            val noteIdx = (i / noteLen) % notes.size
            val posInNote = i % noteLen
            val t = posInNote.toDouble() / sampleRate
            val freq = notes[noteIdx]
            val env = exp(-t * 2.5)
            val tone = (sin(2.0 * PI * freq * t) + 0.3 * sin(2.0 * PI * freq * 2 * t)) * env * 18000.0
            samples[i] = tone.toInt().coerceIn(-32768, 32767).toShort()
        }
        writeWavFile(file, sampleRate, samples)
    }

    private fun generateOcean(file: File) {
        val sampleRate = 44100
        val totalSamples = sampleRate * 12
        val samples = ShortArray(totalSamples)
        var brown = 0.0
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val swell1 = (sin(2.0 * PI * t / 8.0) + 1.0) / 2.0
            val swell2 = (cos(2.0 * PI * t / 12.0) + 1.0) / 2.0
            val combinedSwell = (0.7 * swell1 + 0.3 * swell2)
            val white = Random.nextDouble(-1.0, 1.0)
            brown = (brown + (0.02 * white)) / 1.02
            val sample = brown * 22000.0 * (0.15 + 0.85 * combinedSwell)
            samples[i] = sample.toInt().coerceIn(-32768, 32767).toShort()
        }
        writeWavFile(file, sampleRate, samples)
    }

    private fun generateStream(file: File) {
        val sampleRate = 44100
        val totalSamples = sampleRate * 10
        val samples = ShortArray(totalSamples)
        var pink = 0.0
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val white = Random.nextDouble(-1.0, 1.0)
            pink = 0.92 * pink + 0.08 * white
            val streamMod = 0.7 + 0.3 * sin(2.0 * PI * 3.5 * t) * cos(2.0 * PI * 1.2 * t)
            var sample = pink * 14000.0 * streamMod
            if (Random.nextDouble() < 0.02) {
                val bubbleFreq = Random.nextDouble(900.0, 2400.0)
                val bt = (i % 150).toDouble() / sampleRate
                sample += sin(2.0 * PI * bubbleFreq * bt) * exp(-bt * 200.0) * 8000.0
            }
            samples[i] = (samples[i] + sample.toInt()).coerceIn(-32768, 32767).toShort()
        }
        writeWavFile(file, sampleRate, samples)
    }

    private fun generateCampfire(file: File) {
        val sampleRate = 44100
        val totalSamples = sampleRate * 10
        val samples = ShortArray(totalSamples)
        var brown = 0.0
        for (i in 0 until totalSamples) {
            val white = Random.nextDouble(-1.0, 1.0)
            brown = (brown + (0.03 * white)) / 1.03
            var sample = brown * 7000.0
            if (Random.nextDouble() < 0.004) {
                val popFreq = Random.nextDouble(2000.0, 7000.0)
                val pt = (i % 100).toDouble() / sampleRate
                sample += sin(2.0 * PI * popFreq * pt) * exp(-pt * 600.0) * Random.nextDouble(18000.0, 28000.0)
            }
            samples[i] = (samples[i] + sample.toInt()).coerceIn(-32768, 32767).toShort()
        }
        writeWavFile(file, sampleRate, samples)
    }

    private fun generateCrickets(file: File) {
        val sampleRate = 44100
        val totalSamples = sampleRate * 8
        val samples = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val chirpCycle = sin(2.0 * PI * 4.0 * t)
            val env = if (chirpCycle > 0.7) (chirpCycle - 0.7) * 3.33 else 0.0
            val chirp = sin(2.0 * PI * 4800.0 * t) * env * 12000.0
            samples[i] = chirp.toInt().coerceIn(-32768, 32767).toShort()
        }
        writeWavFile(file, sampleRate, samples)
    }

    private fun generateWind(file: File) {
        val sampleRate = 44100
        val totalSamples = sampleRate * 10
        val samples = ShortArray(totalSamples)
        var pink = 0.0
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val windGust = 0.5 + 0.5 * sin(2.0 * PI * t / 5.0)
            val white = Random.nextDouble(-1.0, 1.0)
            pink = 0.97 * pink + 0.03 * white
            val sample = pink * 16000.0 * windGust
            samples[i] = sample.toInt().coerceIn(-32768, 32767).toShort()
        }
        writeWavFile(file, sampleRate, samples)
    }

    private fun generateAirConditioner(file: File) {
        val sampleRate = 44100
        val totalSamples = sampleRate * 6
        val samples = ShortArray(totalSamples)
        var pink = 0.0
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val white = Random.nextDouble(-1.0, 1.0)
            pink = 0.95 * pink + 0.05 * white
            val motor = sin(2.0 * PI * 100.0 * t) * 3000.0
            val sample = pink * 14000.0 + motor
            samples[i] = sample.toInt().coerceIn(-32768, 32767).toShort()
        }
        writeWavFile(file, sampleRate, samples)
    }

    private fun generateTrain(file: File) {
        val sampleRate = 44100
        val totalSamples = sampleRate * 8
        val samples = ShortArray(totalSamples)
        var brown = 0.0
        val clackInterval = (sampleRate * 0.6).toInt()
        for (i in 0 until totalSamples) {
            val white = Random.nextDouble(-1.0, 1.0)
            brown = (brown + (0.02 * white)) / 1.02
            var sample = brown * 10000.0
            val pos = i % clackInterval
            if (pos < sampleRate * 0.05) {
                val t = pos.toDouble() / sampleRate
                sample += sin(2.0 * PI * 180.0 * t) * exp(-t * 50.0) * 14000.0
            }
            samples[i] = sample.toInt().coerceIn(-32768, 32767).toShort()
        }
        writeWavFile(file, sampleRate, samples)
    }

    private fun generateAirplane(file: File) {
        val sampleRate = 44100
        val totalSamples = sampleRate * 8
        val samples = ShortArray(totalSamples)
        var brown = 0.0
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val white = Random.nextDouble(-1.0, 1.0)
            brown = (brown + (0.01 * white)) / 1.01
            val engineHum = sin(2.0 * PI * 140.0 * t) * 4000.0 + sin(2.0 * PI * 280.0 * t) * 2000.0
            val sample = brown * 15000.0 + engineHum
            samples[i] = sample.toInt().coerceIn(-32768, 32767).toShort()
        }
        writeWavFile(file, sampleRate, samples)
    }

    private fun generateClock(file: File) {
        val sampleRate = 44100
        val totalSamples = sampleRate * 6
        val samples = ShortArray(totalSamples)
        val tickInterval = sampleRate
        for (i in 0 until totalSamples) {
            val pos = i % tickInterval
            var sample = 0.0
            if (pos < sampleRate * 0.02) {
                val t = pos.toDouble() / sampleRate
                val freq = if ((i / tickInterval) % 2 == 0) 3000.0 else 2400.0
                sample = sin(2.0 * PI * freq * t) * exp(-t * 400.0) * 18000.0
            }
            samples[i] = sample.toInt().coerceIn(-32768, 32767).toShort()
        }
        writeWavFile(file, sampleRate, samples)
    }
}
