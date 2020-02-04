<h1 align="center">
<br>
  <img src="screenshots/kotlin.png" width="300" alt="Kotlin Pokedex">
<br>
<br>
Pokedex app built with the navigation from Aircall
</h1>

<p align="center">
  <a href="https://github.com/KotlinBy/awesome-kotlin">
    <img src="https://kotlin.link/awesome-kotlin.svg" alt="Awesome Kotlin">
  </a>
  
  <a href="https://opensource.org/licenses/MIT">
    <img src="https://img.shields.io/badge/License-MIT-red.svg" alt="License MIT">
  </a>

</p>

## Screenshots

<p align="center">
  <img src="screenshots/home.png" width="270" alt="Home">
  <img src="screenshots/pokedex.png" width="270" alt="Pokedex">
  <img src="screenshots/pokedex-fab.png" width="270" alt="Pokedex FAB">
</p>

<p align="center">
  <img src="screenshots/pokedex-fab-search.png" width="270" alt="Pokedex Search">
  <img src="screenshots/pokedex-fab-generation.png" width="270" alt="Pokedex Generation">
  <img src="screenshots/pokemon-info-about.png" width="270" alt="Pokemon Info - About">
</p>

<p align="center">
  <img src="screenshots/pokemon-info-base-stats.png" width="270" alt="Pokemon Info - Base Stats">
  <img src="screenshots/pokemon-info-evolution.png" width="270" alt="Pokemon Info - Evolution">
  <img src="screenshots/news-detail.png" width="270" alt="News Detail">
</p>

## Development Roadmap

- [x] Use the aircall-navigation to start an activity
- [x] Use the aircall-navigation to replace a fragment. Done in the detail of a pokemon. This part can obviously be done in the simpler way with a ViewPager. The goal here is just to showcase how to navigate between fragment with this navigation.
- [x] Use the aircall-navigation to open a BottomSheetFragment
- [ ] Use the aircall-navigation to start an activity for result from an activity
- [ ] Use the aircall-navigation to start an activity for result from a fragment
- [ ] Use the aircall-navigation to navigate between fragment inside a BottomSheetFragment
- [ ] Use the aircall-navigation to open external app like emails, camera, etc...
- [ ] Use the aircall-navigation to propagate event to the activities/fragments in the backstack

## Notes

In this sample, the router is a singleton. In Aircall application we use Dagger to inject it where we need, but the goal here was to show that no external library is required to use it.

## Thanks
- [mrcsxsiq](https://github.com/mrcsxsiq/Kotlin-Pokedex) I used mrcsxsiq's project and replaced the navigation from architecture component by our own

## Design

- [Saepul Nahwan](https://dribbble.com/saepulnahwan23) for his [Pokedex App design](https://dribbble.com/shots/6545819-Pokedex-App)

## Other Pokedex Projects

- [mrcsxsiq](https://github.com/mrcsxsiq/Kotlin-Pokedex) - Pokedex app built with Kotlin
- [Zsolt Kocsi](https://github.com/zsoltk/compose-pokedex) - Android on Jetpack Compose
- [Pham Sy Hung](https://github.com/scitbiz/flutter_pokedex/) - Flutter


## License

All the code available under the MIT license. See [LICENSE](LICENSE).

```
MIT License

Copyright (c) 2019 Marcos Paulo Farias

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
