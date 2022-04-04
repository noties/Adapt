package io.noties.adapt.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.noties.adapt.AdaptException;
import io.noties.adapt.Item;
import io.noties.adapt.wrapper.ItemWrapper;
import io.noties.adapt.util.ExceptionUtil;

import static io.noties.adapt.view.AdaptView.ID_HOLDER;
import static io.noties.adapt.view.AdaptView.ID_ITEM;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class AdaptViewTest {

    private Context context;
    private ViewGroup group;

    @NonNull
    private AdaptView adaptView() {
        return AdaptView.init(group);
    }

    @NonNull
    private AdaptView adaptView(@NonNull AdaptView.Configurator configurator) {
        return AdaptView.init(group, configurator);
    }

    @Before
    public void before() {
        context = mock(Context.class, RETURNS_MOCKS);
        group = mock(ViewGroup.class);

        when(group.getContext())
                .thenReturn(context);

        // mockito cannot mock LayoutInflater here, as it is casted dynamically (no type info in signature)
        when(context.getSystemService(eq(Context.LAYOUT_INFLATER_SERVICE)))
                .thenReturn(mock(LayoutInflater.class));
    }

    @Test
    public void layoutInflater_fromConfigurator() {
        final LayoutInflater inflater = mock(LayoutInflater.class);
        final AdaptView adaptView = adaptView(new AdaptView.Configurator() {
            @Override
            public void configure(@NonNull AdaptView.Configuration configuration) {
                configuration.layoutInflater(inflater);
            }
        });
        Assert.assertEquals(inflater, adaptView.inflater());
    }

    @Test
    public void layoutInflater_fromViewGroup() {

        final LayoutInflater inflater = mock(LayoutInflater.class);
        when(context.getSystemService(eq(Context.LAYOUT_INFLATER_SERVICE))).thenReturn(inflater);

        final AdaptView adaptView = adaptView();
        final LayoutInflater layoutInflater = adaptView.inflater();

        verify(group, atLeast(1)).getContext();
        verify(context, times(1)).getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Assert.assertEquals(inflater, layoutInflater);
    }

    @Test
    public void noItem_createsMockView() {
        // when no item is specified via configuration, a new empty view is created and added to parent
        //  meanwhile item is `null`

        final AdaptView adaptView = adaptView();
        final View view = adaptView.view();

        verify(group, times(1)).addView(eq(view));

        Assert.assertNull(adaptView.item());
    }

    @Test
    public void configuration_item() {

        final Item<Item.Holder> item;
        final Item.Holder holder;
        final View itemView;
        {
            final MockItem<Item<Item.Holder>> mockItem = mockItem();
            item = mockItem.item;
            holder = mockItem.holder;
            itemView = mockItem.itemView;
        }

        final AdaptView adaptView = adaptView(new AdaptView.Configurator() {
            @Override
            public void configure(@NonNull AdaptView.Configuration configuration) {
                configuration.item(item);
            }
        });

        Assert.assertEquals(item, adaptView.item());

        verify(item, times(1)).createHolder(any(LayoutInflater.class), eq(group));
        verify(holder, times(1)).itemView();
        verify(itemView, times(1)).setTag(eq(ID_HOLDER), eq(holder));
        verify(itemView, times(1)).setTag(eq(ID_ITEM), eq(item));
        verify(group, times(1)).addView(eq(itemView));
        verify(item, times(1)).bind(eq(holder));
    }

    @Test
    public void setItem_null_previousNull() {
        // when there is no item and setItem is called with `null` value, no view added/removed

        assertSetItemNull_previousNull(new Accept() {
            @Override
            public void accept(@NonNull AdaptView adaptView) {
                adaptView.setItem(null);
            }
        });
    }

    @Test
    public void setItem_null() {
        // previous item is present
        assertSetItemNull(new Accept() {
            @Override
            public void accept(@NonNull AdaptView adaptView) {
                adaptView.setItem(null);
            }
        });
    }

    @Test
    public void setItem_sameType() {
        // already created view is bound with a new item/holder

        assertSetItem_sameType(new Accept2() {
            @Override
            public void accept2(@NonNull AdaptView adaptView, @NonNull Item<?> item) {
                adaptView.setItem(item);
            }
        });
    }

    @Test
    public void setItem_differentType() {
        // new view created

        assertSetItem_differentType(new Accept2() {
            @Override
            public void accept2(@NonNull AdaptView adaptView, @NonNull Item<?> item) {
                adaptView.setItem(item);
            }
        });
    }

    @Test
    public void setItems_zero() {
        // when items is empty, then null item is used
        // the same as calling `setItem(null)`

        assertSetItemNull(new Accept() {
            @Override
            public void accept(@NonNull AdaptView adaptView) {
                adaptView.setItems(Collections.<Item<?>>emptyList());
            }
        });
    }

    @Test
    public void setItems_zero_previousNull() {
        assertSetItemNull_previousNull(new Accept() {
            @Override
            public void accept(@NonNull AdaptView adaptView) {
                adaptView.setItems(Collections.<Item<?>>emptyList());
            }
        });
    }

    @Test
    public void setItems_single_differentType() {
        // the same as calling `setItem(items.get(0))`
        assertSetItem_differentType(new Accept2() {
            @Override
            public void accept2(@NonNull AdaptView adaptView, @NonNull Item<?> item) {
                adaptView.setItems(Collections.<Item<?>>singletonList(item));
            }
        });
    }

    @Test
    public void setItems_single_sameType() {
        assertSetItem_sameType(new Accept2() {
            @Override
            public void accept2(@NonNull AdaptView adaptView, @NonNull Item<?> item) {
                adaptView.setItems(Collections.<Item<?>>singletonList(item));
            }
        });
    }

    @Test
    public void setItems_multiple() {
        // fails with exception

        final List<Item<?>> items = new ArrayList<Item<?>>() {{
            add(mock(Item.class));
            add(mock(Item.class));
        }};

        Assert.assertEquals(2, items.size());

        final AdaptView adaptView = adaptView();

        try {
            adaptView.setItems(items);
            fail();
        } catch (AdaptException e) {
            ExceptionUtil.assertContains(e, "AdaptView can hold at most one item");
        }
    }

    @Test
    public void setItem_sameTypeButWrapped_notTheSame() {
        // same item type but wrapped must be considered a new item and new view must be created

        final class Wrapped extends ItemWrapper {
            Wrapped(@NonNull Item<?> item) {
                super(item);
            }
        }

        final MockItem<Item<Item.Holder>> current = mockItem();

        final AdaptView adaptView = adaptView(new AdaptView.Configurator() {
            @Override
            public void configure(@NonNull AdaptView.Configuration configuration) {
                configuration.item(current.item);
            }
        });

        final int index = 87;
        when(group.indexOfChild(eq(current.itemView))).thenReturn(index);

        final MockItem<Item<Item.Holder>> item = mockItem();
        final Item<?> wrapped = new Wrapped(item.item);

        adaptView.setItem(wrapped);

        verify(group, times(1)).indexOfChild(eq(current.itemView));
        verify(group, times(1)).removeViewAt(eq(index));
        verify(group, times(1)).addView(eq(item.itemView), eq(index));
    }

    @NonNull
    private static MockItem<Item<Item.Holder>> mockItem() {
        //noinspection unchecked,rawtypes
        final Class<Item<Item.Holder>> type = (Class) Item.class;
        return mockItem(type);
    }

    @NonNull
    private static <I extends Item<Item.Holder>> MockItem<I> mockItem(@NonNull Class<I> type) {
        // `CALLS_REAL_METHODS` to track setTag/getTag
        final View view = mock(View.class, CALLS_REAL_METHODS);
        final Item.Holder holder = mock(Item.Holder.class);
        final I item = mock(type);
        when(holder.itemView()).thenReturn(view);
        when(item.createHolder(any(LayoutInflater.class), any(ViewGroup.class))).thenReturn(holder);
        return new MockItem<>(view, holder, item);
    }

    private static class MockItem<I extends Item<Item.Holder>> {
        final View itemView;
        final Item.Holder holder;
        final I item;

        MockItem(@NonNull View itemView, @NonNull Item.Holder holder, @NonNull I item) {
            this.itemView = itemView;
            this.holder = holder;
            this.item = item;
        }
    }

    interface Accept {
        void accept(@NonNull AdaptView adaptView);
    }

    private void assertSetItemNull_previousNull(@NonNull Accept setItem) {
        final AdaptView adaptView = adaptView();
        final View view = adaptView.view();

        Assert.assertNotNull(view);
        Assert.assertNull(adaptView.item());

        setItem.accept(adaptView);

        // view is the same
        Assert.assertEquals(view, adaptView.view());

        verify(group, never()).removeViewAt(anyInt());
        verify(group, never()).addView(any(View.class), anyInt(), any(ViewGroup.LayoutParams.class));
    }

    private void assertSetItemNull(@NonNull Accept setItem) {

        final Item<?> item = mockItem().item;

        final AdaptView adaptView = adaptView(new AdaptView.Configurator() {
            @Override
            public void configure(@NonNull AdaptView.Configuration configuration) {
                configuration.item(item);
            }
        });
        final View view = adaptView.view();

        Assert.assertNotNull(view);
        Assert.assertEquals(item, adaptView.item());

        final int index = 0;
        when(group.indexOfChild(eq(view))).thenReturn(index);

        setItem.accept(adaptView);

        // view is changed
        Assert.assertNotEquals(adaptView.view(), view);
        Assert.assertNull(adaptView.item());

        verify(group, times(1)).removeViewAt(eq(index));
        verify(group, times(1)).addView(any(View.class), eq(index));
    }

    interface Accept2 {
        void accept2(@NonNull AdaptView adaptView, @NonNull Item<?> item);
    }

    private void assertSetItem_differentType(@NonNull Accept2 accept2) {

        final MockItem<Item<Item.Holder>> mock = mockItem();
        final Item<Item.Holder> item = mock.item;
        final AdaptView adaptView = adaptView(new AdaptView.Configurator() {
            @Override
            public void configure(@NonNull AdaptView.Configuration configuration) {
                configuration.item(item);
            }
        });

        Assert.assertEquals(item, adaptView.item());

        abstract class OtherItem extends Item<Item.Holder> {
            OtherItem(long id) {
                super(id);
            }
        }

        final MockItem<OtherItem> otherMock = mockItem(OtherItem.class);

        // ensure viewTypes are different
        Assert.assertNotEquals(item.viewType(), otherMock.item.viewType());

        final int index = 4;
        when(group.indexOfChild(eq(mock.itemView))).thenReturn(index);

        accept2.accept2(adaptView, otherMock.item);

        verify(otherMock.item, times(1)).createHolder(any(LayoutInflater.class), any(ViewGroup.class));
        verify(group, times(1)).indexOfChild(eq(mock.itemView));
        verify(group, times(1)).removeViewAt(eq(index));
        verify(group, times(1)).addView(eq(otherMock.itemView), eq(index));

        Assert.assertNotEquals(adaptView.view(), mock.itemView);
        Assert.assertEquals(otherMock.itemView, adaptView.view());
        Assert.assertEquals(otherMock.item, adaptView.item());
    }

    private void assertSetItem_sameType(@NonNull Accept2 accept2) {

        final MockItem<Item<Item.Holder>> mock = mockItem();
        final AdaptView adaptView = adaptView(new AdaptView.Configurator() {
            @Override
            public void configure(@NonNull AdaptView.Configuration configuration) {
                configuration.item(mock.item);
            }
        });

        Assert.assertEquals(mock.item, adaptView.item());

        //noinspection unchecked
        final Item<Item.Holder> other = mock(Item.class);

        accept2.accept2(adaptView, other);

        // existing holder (from mock) will be bound to this other item (no add/remove in ViewGroup)
        verify(group, never()).removeViewAt(anyInt());
        verify(group, never()).addView(any(View.class), anyInt());

        verify(other, never()).createHolder(any(LayoutInflater.class), any(ViewGroup.class));
        verify(other, times(1)).bind(eq(mock.holder));
    }
}