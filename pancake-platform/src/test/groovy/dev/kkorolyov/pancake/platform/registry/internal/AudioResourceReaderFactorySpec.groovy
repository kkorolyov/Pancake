package dev.kkorolyov.pancake.platform.registry.internal

import dev.kkorolyov.pancake.platform.Resources
import dev.kkorolyov.pancake.platform.media.audio.Audio
import dev.kkorolyov.pancake.platform.media.audio.AudioFactory
import dev.kkorolyov.pancake.platform.registry.Registry
import dev.kkorolyov.simplefuncs.convert.Converter

import spock.lang.Shared
import spock.lang.Specification

import static dev.kkorolyov.simplespecs.SpecUtilities.setField

class AudioResourceReaderFactorySpec extends Specification {
	@Shared
	String[] uris = ["file://local/path", "https://remote/path", "rando"]

	AudioResourceReaderFactory factory = new AudioResourceReaderFactory()

	AudioFactory audioFactory = Mock()

	Registry<String, Audio> registry = new Registry<>()
	Converter<String, Optional<Audio>> converter

	def setup() {
		setField("AUDIO_FACTORY", Resources, audioFactory)

		converter = factory.get(registry)
	}

	def "reads path"() {
		1 * audioFactory.get(uri) >> audio

		expect:
		converter.convert(uri).orElse(null) == audio

		where:
		uri << uris
		audio << uris.collect { Mock(Audio) }
	}
}