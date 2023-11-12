(() => {
    // the event is fired by the server after vod details have been sent
    document.body.addEventListener('browse-modal-vod-settled', event => {
        let videoTag = document.querySelector("#browse-modal-vod-container video")

        let player = videojs(videoTag.getAttribute("id"))
        player.nuevo({
            video_id: videoTag.getAttribute("videoId"),
            videoInfo: true,
            ...(videoTag.hasAttribute("videoTitle") && {infoTitle: videoTag.getAttribute("videoTitle")}),
            ...(videoTag.hasAttribute("videoDescription") && {infoDescription: videoTag.getAttribute("videoDescription")}),
            ...(videoTag.hasAttribute("slideImage") && {slideImage: videoTag.getAttribute("slideImage")}),
            ...(videoTag.hasAttribute("slideImage") && {ghostThumb: true}),
            pipButton: false,
            shareMenu: false,
            zoomMenu: false,
            buttonForward: true,
            resume: true,
            snapshot: true,
            snapshotType: "png",
        });
        player.hotkeys({
            volumeStep: 0.1,
            seekStep: 5
        });
        player.chromecast({button: "controlbar"});
    })

    document.querySelector("#browse-modal-vod-container").addEventListener('hide.bs.modal', event => {
        let videoTag = event.target.querySelector("video")
        videojs(videoTag.getAttribute("id")).dispose();
    })

    document.body.addEventListener("selectpicker-render", function (evt) {
        // alert("before")
        let $selectpicker = $('.htmx-settling .selectpicker');
        $selectpicker.selectpicker('render');
    })
})()
