# Config GUI

The config GUI is the main selling point of MoulConfig.

## Structure

The config structure is roughly mapped to Java classes. Each category, subcategory and accordion is its own Java object.

The base class needs to extend `Config` and each field needs to be non-static. This way your config is also easily
serializable as a Json Object. MoulConfig is however completely agnostic towards your configs save format. The only
requirement is that instances do not get reassigned. So updating the config object after you have processed a config
requires that config to be reprocessed (and old MoulConfig to be discarded).

### Top Level Structure

You can specify categories
using [`@Category`](../javadocs/common/io.github.moulberry.moulconfig.annotations/-category/index.html).
You can even nest categories once to create subcategories. Subsubcategories however do not work.

```java
public class MyConfig extends Config {
    @Override
    public String getTitle() {
        return "§bMyMod Config";
    }

    @Category(name = "Category Name", desc = "Category Description")
    public MyCategory myCategory = new MyCategory();

    public static class MyCategory {
        @Category(name = "SubCategory", desc = "Sub category description")
        public MySubCategory subCategory = new MySubCategory();
    }
}
```

Note that even tho i sometimes use static inner classes, you can have any sort of class (even including non-static
inner classes) as your structure.

### Inside each category

Inside each category you can specify config options
using [`@ConfigOption`](../javadocs/common/io.github.moulberry.moulconfig.annotations/-config-option/index.html).

In addition to the config option which contains meta information like the name, you will need to add another annotation
of your choice to specify an editor for that variable. Check out all
the [annotations](../javadocs/common/io.github.moulberry.moulconfig.annotations/index.html).

Make sure that you check the Javadoc on each annotation to know which type your field needs to have for it to work.

```java
public class MySubCategory {
    @ConfigOption(name = "Text Test", desc = "Text Editor Test")
    @ConfigEditorText
    public String text = "Text";

    @ConfigOption(name = "Number", desc = "Slider test")
    @ConfigEditorSlider(minValue = 0, maxValue = 10, minStep = 1)
    public int slider = 0;


    @ConfigOption(name = "Key Binding", desc = "Key Binding")
    @ConfigEditorKeybind(defaultKey = Keyboard.KEY_F)
    public int keyBoard = Keyboard.KEY_F;
}
```

### Accordions

Sometimes just subcategories are not enough and you will want to group your options even
further. [`@Accordion`s](../javadocs/common/io.github.moulberry.moulconfig.annotations/-accordion/index.html)
allow you to nest options arbitrarily deep.

```java
public class MySubCategory {
    @ConfigOption(name = "Text Test", desc = "Text Editor Test")
    @ConfigEditorText
    public String text = "Text";

    @Accordion
    @ConfigOption(name = "Hehe", desc = "hoho")
    public MyAccordion myAccordion = new MyAccordion();

    public static class MyAccordion {
        @ConfigOption(name = "Number", desc = "Slider test")
        @ConfigEditorSlider(minValue = 0, maxValue = 10, minStep = 1)
        public int slider = 0;


        @ConfigOption(name = "Key Binding", desc = "Key Binding")
        @ConfigEditorKeybind(defaultKey = Keyboard.KEY_F)
        public int keyBoard = Keyboard.KEY_F;
    }
}
```

### Properties

Sometimes you want to listen to changes to a config variable and run some updates based on that. For that you can use
[`Property<T>`](../javadocs/common/io.github.moulberry.moulconfig.observer/-property/index.html). Check the Javadoc for
more information on how to use Properties.

