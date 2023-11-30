(() => {
    document.addEventListener("DOMContentLoaded", function () {
        let videoTag = document.querySelector("video")

        let player = videojs(videoTag.getAttribute("id"))
        player.nuevo({
            video_id: videoTag.getAttribute("videoId"),
            ...(videoTag.hasAttribute("slideImage") && {slideImage: videoTag.getAttribute("slideImage")}),
            ...(videoTag.hasAttribute("slideImage") && {ghostThumb: true}),
            pipButton: false,
            shareMenu: false,
            zoomMenu: false,
            buttonForward: true,
            resume: true,
            theaterButton: true,
        });

        try {
            player.hotkeys({
                volumeStep: 0.1,
                seekStep: 5
            });
        } catch (error) {
            console.error(error)
        }

        try {
            player.chromecast({button: "controlbar"});
        } catch (error) {
            console.error(error)
        }

        player.on('mode', function (event, mode) {
            toggleVideoTheaterMode(mode)
        });
    });

    function toggleVideoTheaterMode(mode) {
        if (mode === 'large') {
            document.getElementById("theme-header-img").classList.remove('d-md-block');
            document.getElementById("chat").classList.remove('d-md-block');
            document.getElementById("vod-column").classList.remove('col-md-8');
            document.getElementById("vod-column").classList.remove('col-md-8');
            document.getElementById("container").classList.replace('container', 'container-xxl');
            document.getElementById("header-padding").classList.add('d-none');
        } else if (mode === 'normal') {
            document.getElementById("theme-header-img").classList.add('d-md-block');
            document.getElementById("chat").classList.add('d-md-block');
            document.getElementById("vod-column").classList.add('col-md-8');
            document.getElementById("vod-column").classList.add('col-md-8');
            document.getElementById("container").classList.replace('container-xxl', 'container');
            document.getElementById("header-padding").classList.remove('d-none');
        }
    }
})()
