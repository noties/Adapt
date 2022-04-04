package io.noties.adapt.wrapper;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import io.noties.adapt.Item;

@RunWith(RobolectricTestRunner.class)
public class IdWrapperTest {

    @Test
    public void test() {
        final Item<?> item = new Item<Item.Holder>(42L) {
            @NonNull
            @Override
            public Holder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
                throw new IllegalStateException();
            }

            @Override
            public void bind(@NonNull Holder holder) {

            }
        };
        final IdWrapper idWrapper = new IdWrapper(item, 24L);
        Assert.assertEquals(42L, item.id());
        Assert.assertEquals(24L, idWrapper.id());
    }
}