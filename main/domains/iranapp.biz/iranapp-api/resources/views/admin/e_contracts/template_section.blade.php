{{-- One section of the contract in the wording editor (admin.e_contracts.template). --}}
<div class="tpl-section">
    <div class="tpl-section-head clearfix">
        <span class="tpl-section-number pull-right"></span>
        <span class="pull-left">
            <button type="button" class="btn btn-default btn-xs tpl-up" title="انتقال به بالا"><i class="fa fa-arrow-up"></i></button>
            <button type="button" class="btn btn-default btn-xs tpl-down" title="انتقال به پایین"><i class="fa fa-arrow-down"></i></button>
            <button type="button" class="btn btn-danger btn-xs tpl-remove" title="حذف بند"><i class="fa fa-trash"></i></button>
        </span>
    </div>
    <input type="text" name="headings[]" class="form-control m-b-10" maxlength="200"
           placeholder="عنوان بند (اختیاری)، مثلا: موضوع قرارداد" value="{{ $heading }}">
    <textarea name="texts[]" class="form-control" rows="4" placeholder="متن بند">{{ $text }}</textarea>
</div>
