package io.noties.adapt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.description;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.noties.adapt.listview.AdaptListView;
import io.noties.adapt.recyclerview.AdaptRecyclerView;
import io.noties.adapt.view.AdaptView;
import io.noties.adapt.viewgroup.AdaptViewGroup;
import io.noties.adapt.viewpager.AdaptViewPager;
import io.noties.adapt.wrapper.IdWrapper;

@RunWith(RobolectricTestRunner.class)
public class AdaptTest {

    @NonNull
    private static ViewGroup createViewGroup() {
        return new FrameLayout(RuntimeEnvironment.getApplication());
    }

    @NonNull
    private static Item<?> createItem() {
        final Item<Item.Holder> item = mock(Item.class);
        final Item.Holder holder = mock(Item.Holder.class);
        final View view = mock(View.class);

        // id is final..
//        when(item.id()).thenReturn((long) System.identityHashCode(item));

        when(item.createHolder(any(), any())).thenReturn(holder);
        when(holder.itemView()).thenReturn(view);
        when(view.getTag(anyInt())).thenReturn(holder);

        return IdWrapper.wrap(System.identityHashCode(item), item);
    }

    @Test
    public void onItemsChangedListener() {
        final List<Adapt> adapts = Arrays.asList(
                AdaptView.init(createViewGroup()),
                AdaptViewGroup.init(createViewGroup()),
                AdaptListView.init(new ListView(RuntimeEnvironment.getApplication())),
                AdaptRecyclerView.init(new RecyclerView(RuntimeEnvironment.getApplication())),
                AdaptViewPager.init(new ViewPager(RuntimeEnvironment.getApplication()))
        );

        final List<List<Item<?>>> inputs = Arrays.asList(
                null,
                Collections.emptyList(),
                Collections.singletonList(createItem()),
                Arrays.asList(createItem(), createItem())
        );

        for (Adapt adapt : adapts) {
            for (List<Item<?>> input : inputs) {
                final Adapt.OnItemsChangedListener listener = mock(Adapt.OnItemsChangedListener.class);
                adapt.registerOnItemsChangedListener(listener);

                try {
                    adapt.setItems(input);
                } catch (Throwable t) {
                    // all adapts should pass, but AdaptView is different, as it allows at most one item...
                    //  it is actually good question if this should have the same interface as main adapt
                    if (size(input) > 1 && adapt instanceof AdaptView) {
                        // this is okay, it is known thing, but we should thing of sharing the same
                        //  interface with it, as it is a little weird one
                    } else {
                        throw t;
                    }
                }

                verify(
                        listener,
                        description("Adapt:" + adapt)
                ).onItemsChanged(eq(input));

                adapt.unregisterOnItemsChangedListener(listener);
            }
        }
    }

    private static int size(@Nullable List<Item<?>> items) {
        return items == null ? 0 : items.size();
    }
}
