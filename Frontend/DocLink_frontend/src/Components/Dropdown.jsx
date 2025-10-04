export default function Dropdown({ value, onChange, options, defaultText }) {
  return (
    <select className="dropdown" value={value} onChange={onChange}>
      <option value="">{defaultText}</option>
      {options.map((opt, i) => (
        <option key={i} value={opt}>
          {opt}
        </option>
      ))}
    </select>
  );
}