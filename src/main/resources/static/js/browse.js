(() => {
    document.body.addEventListener("selectpicker-render", function (evt) {
        // alert("before")
        let $selectpicker = $('.htmx-settling .selectpicker');
        $selectpicker.selectpicker('render');
    })
})()
