$(document).on('wb-ready.wb', function () {

    var localizedName = getLocale();

    if (theTR != null) {
        $('#tfmUsed').val(theTR.totalTFMLiterUsed);
        $('#numOfBars').val(theTR.totalTFMBars);
        $('#bayEC').val(theTR.totalBayluscideEC);
        $('#bayWP').val(theTR.totalBayluscideWP);
        $('#bayGB').val(theTR.totalBayluscideGB);
        $('#adultPM').val(theTR.totalAdultPM);

        var mydata = theTR.totalSpecies;

        if ($.fn.DataTable.isDataTable('#imest_table')) {
            $('#imest_table').DataTable().destroy();
        }

        $('#imest_table tbody').empty();

        $('#imest_table').DataTable({
            "data": theTR.totalSpecies,
            "scrollY": "200px",
            "scrollCollapse": true,
            "paging": false,
            "ordering": false,
            "searching": false,
            "info": false,
            "sDom": 't',
            "aoColumns": [
                {
                    "mData": function (data, type, dataToSet) {
                        return data.split("|")[0];
                    }
                },
                {
                    "mData": function (data, type, dataToSet) {
                        return data.split("|")[1];
                    }
                },
                {
                    "mData": function (data, type, dataToSet) {
                        return data.split("|")[2];
                    }
                }
            ],
            "language": {
                "url": getLanguageURL()
            }
        });
    }
    $("#loader").css("visibility", "hidden");

    $('#button_exit').on("click", function (e) {
        e.preventDefault();
        var redirectUrl = getBaseString() + localizedName + "/treatments";
        window.location.href = redirectUrl;
    });
});