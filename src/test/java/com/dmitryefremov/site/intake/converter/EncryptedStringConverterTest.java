package com.dmitryefremov.site.intake.converter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EncryptedStringConverterTest {

    private final EncryptedStringConverter converter = new EncryptedStringConverter();

    @AfterEach
    void tearDown() {
        EncryptedStringConverter.resetSecretKey();
    }

    @Test
    void testNullHandling() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @Test
    void testEncryptionAndDecryptionRoundTrip() {
        String originalText = "Confidential client intake wishlist payload content";
        String encrypted = converter.convertToDatabaseColumn(originalText);

        assertThat(encrypted).isNotNull();
        assertThat(encrypted).isNotEqualTo(originalText);

        String decrypted = converter.convertToEntityAttribute(encrypted);
        assertThat(decrypted).isEqualTo(originalText);
    }

    @Test
    void testEncryptionProducesDifferentCiphertextsForSameInputDueToIV() {
        String originalText = "Confidential client intake wishlist payload content";
        String encrypted1 = converter.convertToDatabaseColumn(originalText);
        String encrypted2 = converter.convertToDatabaseColumn(originalText);

        assertThat(encrypted1).isNotEqualTo(encrypted2);

        assertThat(converter.convertToEntityAttribute(encrypted1)).isEqualTo(originalText);
        assertThat(converter.convertToEntityAttribute(encrypted2)).isEqualTo(originalText);
    }
}
