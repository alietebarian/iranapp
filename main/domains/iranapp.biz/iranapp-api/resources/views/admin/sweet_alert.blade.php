{{--
    Pop-ups for the success_msg / error_msg the controllers flash (an object with title and msg).
    The session is stored as JSON (config/session.php 'serialization' => 'json'), so by the time
    the message is read on the next request it has become an array; both shapes are accepted.
--}}
@foreach(['success_msg' => 'success', 'error_msg' => 'error'] as $flashKey => $swalType)
    @if(session($flashKey))
        @php
            $flash = (array) session($flashKey);
        @endphp
        <script>
            $(document).ready(function () {
                setTimeout(function () {
                    swal(
                        "{{ $flash['title'] ?? '' }}",
                        "{!! $flash['msg'] ?? '' !!}" ,
                        "{{ $swalType }}"
                    );
                } , 400);
            });
        </script>
    @endif
@endforeach
