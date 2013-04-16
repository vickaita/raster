# raster

**This software should be considered pre-alpha. I am still working out the API
and everything is subject to change.**

A ClojureScript library designed to simplify interaction with raster graphics in
HTML documents. This library provides functions for creating, manipulating, and
displaying ImageData objects.

## Project Goals

The library provides implementations of many ClojureScript protocols for
ImageData so that you can use many existing functions such as map, filter,
reduce, nth, get, etc.

Allowing versatile and idiomatic access to ImageData is the primary goal of this
library.

Of secondary concern is performance. Whenever possible, efficient methods for
operating on ImageData are choosen, but not to the detriment of a flexible API.

## Usage

### Creating an ImageData object

Create an ImageData object with the `image-data` function provided in
`vickaita.raster.core`. You can pass it an img element, a canvas element, or a
2d canvas context.

    (ns raster.example
      (:require [vickaita.raster.core :as r]
                [goog.dom :as dom]))

    ;; From an HTMLImageElement
    (r/image-data (dom/getElement "an-img"))

    ;; From an HTMLCanvasElement
    (r/image-data (dom/getElement "a-canvas"))

    ;; From a map literal
    (r/image-data {:width 10
                   :height 10
                   :data (take 400 (repeatedly (partial rand-int 256)))})

### dopixel macro

The `dopixel` macro operates on an image one pixel at a time.

    (dopixel [[r g b a] img]
             [(+ 1 r) (+ 1 g) (+ 1 b) a])

## License

Copyright © 2013 Vick Aita

Distributed under the Eclipse Public License, the same as Clojure.
