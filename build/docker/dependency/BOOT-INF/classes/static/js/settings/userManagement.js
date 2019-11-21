var userManagement = {
    initForm:function () {
        $('#uUName').on("blur", function(){
            $(this).val($(this).val().toLowerCase());
            $(this).val($(this).val().replace(/\s+/g, ''));
        });

        convertDataTextOnly();
    },
    goToEdit : function(id){
        window.location.href = editPath + '?id='+ id;
    },
    activateUser: function(id, description, active) {
        $('#successMessage').hide();
        $.confirmAction(function() {
            $.processPromise(new Promise(function(doReturn) {
                $.ajax({
                    url: activatePath+"?id="+id,
                    type: 'PATCH',
                    success: function(result){doReturn(result === 'success')},
                    error: function(){doReturn(false)}
                });
            }), function(result) {
                if (result) {
                    var activateButton = $('#activate_'+id);
                    if ($(activateButton).length) {
                        $(activateButton).attr('href', 'javascript: userManagement.activateUser('+id+',"'+description+'",'+!active+')');
                        $(activateButton).attr('title', $.getMessage('js.icon.'+(!active ? 'activate' : 'deactivate')+'.title'));
                        $(activateButton).find('span').first().attr('class', 'glyphicon glyphicon-'+(!active ? 'ok-circle text-green' : 'ban-circle text-red'));
                    }
                } else {
                    $.showAlert('danger', $.getMessage('js.error.serverDB_operation_failed'), 10000, 'user-container');
                }
            });
        }, 'js.confirm.'+(!active ? 'activate' : 'deactivate'), [description])
    }
};

$(document).on('wb-ready.wb', function() {
    $.initForPathContains('/create', userManagement.initForm);
    $.initForPathContains('/edit', userManagement.initForm);
});