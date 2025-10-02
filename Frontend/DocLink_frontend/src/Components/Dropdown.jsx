export default function Dropdown({ options, defaultText }) {
  return (
    <select className="dropdown">
      <option>{defaultText}</option>
      {options.map((opt, i) => (
        <option key={i} value={opt}>
          {opt}
        </option>
      ))}
    </select>
  );
}
