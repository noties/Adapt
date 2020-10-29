package ru.noties.adapt;

import androidx.annotation.NonNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class AdaptSourceTest {

    @Test
    public void key_provider_returns_hash() {

        final AdaptSource.KeyProvider keyProvider = new AdaptSource.KeyProvider();

        final Class<?>[] types = {
                String.class,
                Integer.class,
                AdaptSourceTest.class
        };

        for (Class<?> type: types) {
            assertEquals(type.hashCode(), keyProvider.provideKey(type));
        }
    }

    @Test
    public void nothing_added_build_throws() {
        try {
            new AdaptSource.Builder<CharSequence>(new AdaptSource.KeyProvider())
                    .build();
            assertTrue(false);
        } catch (AdaptConfigurationError e) {
            assertTrue(true);
        }
    }

    @Test
    public void attempt_adding_duplicate() {

        final AdaptSource.Builder<CharSequence> builder = new AdaptSource.Builder<>(new AdaptSource.KeyProvider());

        //noinspection unchecked
        assertTrue(builder.append(CharSequence.class, mock(AdaptEntry.class)));

        //noinspection unchecked
        assertFalse(builder.append(CharSequence.class, mock(AdaptEntry.class)));
    }

    @Test
    public void obtain_not_added_throws() {

        final AdaptSource.KeyProvider keyProvider = new AdaptSource.KeyProvider();
        final AdaptSource.Builder<CharSequence> builder = new AdaptSource.Builder<>(keyProvider);
        //noinspection unchecked
        builder.append(CharSequence.class, mock(AdaptEntry.class));

        final AdaptSource<CharSequence> source = builder.build();

//        assertThrows(() -> source.entry(keyProvider.provideKey(CharSequence.class)));
        assertThrows(new Action() {
            @Override
            public void apply() {
                source.entry(keyProvider.provideKey(String.class));
            }
        });

        assertThrows(new Action() {
            @Override
            public void apply() {
                source.entry("Not present");
            }
        });

        assertThrows(new Action() {
            @Override
            public void apply() {
                source.assignedViewType("Not present");
            }
        });

        assertThrows(new Action() {
            @Override
            public void apply() {
                source.assignedViewType(String.class);
            }
        });
    }

    @Test
    public void regular_obtain() {

        final AdaptSource.KeyProvider keyProvider = new AdaptSource.KeyProvider();
        final AdaptSource.Builder<CharSequence> builder = new AdaptSource.Builder<>(keyProvider);

        //noinspection unchecked
        final AdaptEntry<String> string = mock(AdaptEntry.class);
        //noinspection unchecked
        final AdaptEntry<StringBuilder> stringBuilder = mock(AdaptEntry.class);

        //noinspection EqualsBetweenInconvertibleTypes
        assertTrue(!string.equals(stringBuilder));

        assertTrue(builder.append(String.class, string));
        assertTrue(builder.append(StringBuilder.class, stringBuilder));

        final AdaptSource<CharSequence> source = builder.build();

        assertEquals(string, source.entry(""));
        assertEquals(string, source.entry(keyProvider.provideKey(String.class)));
        assertEquals(keyProvider.provideKey(String.class), source.assignedViewType(""));
        assertEquals(keyProvider.provideKey(String.class), source.assignedViewType(String.class));

        assertEquals(stringBuilder, source.entry(new StringBuilder()));
        assertEquals(stringBuilder, source.entry(keyProvider.provideKey(StringBuilder.class)));
        assertEquals(keyProvider.provideKey(StringBuilder.class), source.assignedViewType(new StringBuilder()));
        assertEquals(keyProvider.provideKey(StringBuilder.class), source.assignedViewType(StringBuilder.class));
    }

    private interface Action {
        void apply();
    }

    private static void assertThrows(@NonNull Action action) {
        try {
            action.apply();
            assertTrue(false);
        } catch (AdaptRuntimeError e) {
            assertTrue(true);
        }
    }
}