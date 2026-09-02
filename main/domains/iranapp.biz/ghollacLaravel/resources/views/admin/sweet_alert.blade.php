@if(session('success_msg'))
    <script>
        $(document).ready(function () {
            setTimeout(function () {
                swal(
                    "{{  session('success_msg')->title}}",
                    "{!! session('success_msg')->msg !!}" ,
                    "success"
                );
            } , 400);
        });
    </script>
@endif
@if(session('error_msg'))
    <script>
        $(document).ready(function () {
            setTimeout(function () {
                swal(
                    "{{  session('error_msg')->title }}",
                    "{!! session('error_msg')->msg !!}" ,
                    "error"
                );
            } , 400);
        });
    </script>
@endif

