<script>
    @if($msg = session('success_swal'))
    $(document).ready(function () {
        setTimeout(function () {
            swal("{{ $msg->title }}", "{{ $msg->description }}" , "success")
        }, 400);
    });
    @endif

    @if($msg = session('error_swal'))
    $(document).ready(function () {
        setTimeout(function () {
            swal("{{ $msg->title }}", "{{ $msg->description }}" , "error")
        }, 400);
    });
    @endif


</script>