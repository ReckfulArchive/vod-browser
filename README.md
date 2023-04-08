# Twitch metadata

Different files and scripts that in one way or another are related to [twitch.tv/reckful](https://twitch.tv/reckful).

Files are located in [src/main/resources](src/main/resources) for ease of use in scripts when run on different machines.

Notable:

* [thumbnails](src/main/resources/thumbnails) - a collection of all video thumbnails, where the numbers in the file
  name represent the video id for which the thumbnail was created.
* [all-videos-info](src/main/resources/all-videos-info) - a collection of json descriptions for every [video][1] as of
  April 8th, 2023. Includes both past broadcasts and highlights. Contains some fields that are not present in the old
  archive info.
* [old-archive-info](src/main/resources/old-archive-info) - a collection of json descriptions for VODs that were
  downloaded sometime during the summer of 2020. Describes the files of the main VOD archive (11TB).
* [all-videos-page](src/main/resources/all-videos-page) - a copy of the [all videos page][1] as of April 8th, 2023.
  Includes both HTML contents, images and some other things. Useful for extracting all video ids and thumbnails.

[1]: https://www.twitch.tv/reckful/videos?filter=all
