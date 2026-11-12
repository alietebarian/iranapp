import { useQuery } from '@tanstack/react-query';
import * as api from '@/lib/api';
import { SelectField } from './fields';

export interface LocationValue {
  provinceId: string;
  cityId: string;
  regionId: string;
}

export const EMPTY_LOCATION: LocationValue = { provinceId: '', cityId: '', regionId: '' };

interface Props {
  value: LocationValue;
  onChange: (value: LocationValue) => void;
  /** Estate, vehicle and job ads are placed in a region of the city; business ads stop at the city. */
  withRegion?: boolean;
  /** First row of each picker: «لطفا انتخاب کنید.» in forms, «همه» in filters. */
  placeholder?: string;
  suffix?: string;
}

/** The استان / شهر / منطقه pickers every ad form and filter shares; each one loads from the one above. */
export function LocationSelects({ value, onChange, withRegion = true, placeholder, suffix = '' }: Props) {
  const provinces = useQuery({ queryKey: ['provinces'], queryFn: api.getProvinces, staleTime: Infinity });
  const cities = useQuery({
    queryKey: ['cities', value.provinceId],
    queryFn: () => api.getCities(value.provinceId),
    enabled: value.provinceId !== '',
    staleTime: Infinity,
  });
  const regions = useQuery({
    queryKey: ['regions', value.cityId],
    queryFn: () => api.getRegions(value.cityId),
    enabled: withRegion && value.cityId !== '',
    staleTime: Infinity,
  });
  const options = (rows: Array<{ id: string | number; name: string }> | undefined) => (rows ?? []).map((row) => ({ value: String(row.id), label: row.name }));

  return (
    <>
      <SelectField
        label={`استان${suffix}`}
        placeholder={placeholder}
        value={value.provinceId}
        disabled={!provinces.data}
        options={options(provinces.data)}
        onChange={(provinceId) => onChange({ provinceId, cityId: '', regionId: '' })}
      />
      <SelectField
        label={`شهر${suffix}`}
        placeholder={placeholder}
        value={value.cityId}
        disabled={!value.provinceId || !cities.data}
        options={options(cities.data)}
        onChange={(cityId) => onChange({ ...value, cityId, regionId: '' })}
      />
      {withRegion && (
        <SelectField
          label={`منطقه${suffix}`}
          placeholder={placeholder}
          value={value.regionId}
          disabled={!value.cityId || !regions.data}
          options={options(regions.data)}
          onChange={(regionId) => onChange({ ...value, regionId })}
        />
      )}
    </>
  );
}
