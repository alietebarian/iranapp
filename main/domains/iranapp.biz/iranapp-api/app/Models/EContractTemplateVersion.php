<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

/**
 * One saved version of the electronic contract's wording (admin panel: متن قرارداد الکترونیک).
 * Rows are only ever added; see App\Support\EContractTemplate.
 */
class EContractTemplateVersion extends Model
{
    protected $table = 'e_contract_templates';

    protected $fillable = [
        'version',
        'title',
        'sections',
        'created_by',
    ];

    protected function casts(): array
    {
        return [
            'sections' => 'array',
        ];
    }

    public function creator()
    {
        return $this->belongsTo(Admin::class, 'created_by');
    }
}
