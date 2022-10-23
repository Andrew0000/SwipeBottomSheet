# SwipeBottomSheet

Android View-based BottomSheet that works well with NestedScrollView and RecyclerView.

Example:

![picture](https://raw.githubusercontent.com/Andrew0000/SwipeBottomSheet/master/files/SwipeBottomSheet_example.gif)


# Setup:  

[![](https://jitpack.io/v/Andrew0000/SwipeBottomSheet.svg)](https://jitpack.io/#Andrew0000/SwipeBottomSheet)

1. Add `maven { url 'https://jitpack.io' }` to the `allprojects` or `dependencyResolutionManagement` section in top-level `build.gradle` or `settings.gradle`.  
For example (`settings.gradle`):
```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter() // Warning: this repository is going to shut down soon
        maven { url "https://jitpack.io" }
    }
}
```
2. Add `implementation 'com.github.Andrew0000:SwipeBottomSheet:$latest_version'` to the module-level `build.gradle`  

3. Use it a) in xml:
```

    <crocodile8008.swipe_bottom_sheet.SwipeBottomSheet
        android:id="@+id/swipeBottomSheet"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:elevation="6dp"
        ...>

        <androidx.core.widget.NestedScrollView
            android:layout_width="match_parent"
            android:layout_height="match_parent">

            <LinearLayout
                ...
```
or b) in code:
```
SwipeBottomSheet
            .wrapNested(
                LayoutInflater.from(context).inflate(R.layout.content, layoutRootView, false)
            )
            .apply {
                elevation = 6.toPx()
                clip = SwipeBottomSheet.Clip(
                    paddingTop = 80f.toPx(),
                    cornersRadiusTop = 40f.toPx(),
                )
                addSwipeFinishListener { removeBottomSheet() }
                animateAppearance(parentView = layoutRootView)
            }
```
