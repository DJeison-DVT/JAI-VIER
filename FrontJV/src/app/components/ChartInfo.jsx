// components/ChartInfo.jsx
import React from 'react';
import { colors } from '../utils/projectData';

const ChartInfo = () => {
  return (
    <div className="mt-6 p-4 bg-yellow-50 border border-yellow-200 rounded">
      <h3 className="font-bold text-yellow-800">Interpretación del Burn-down Chart</h3>
      <ul className="mt-2 text-sm text-yellow-800 list-disc pl-5">
        <li>La <span className="font-bold" style={{color: colors.ideal}}>línea ideal</span> muestra cómo debería disminuir el trabajo si el progreso fuera constante.</li>
        <li>La <span className="font-bold" style={{color: colors.actual}}>línea real</span> muestra el trabajo realmente restante en cada momento.</li>
        <li>Si la línea real está por encima de la ideal, el proyecto está retrasado.</li>
        <li>Si la línea real está por debajo de la ideal, el proyecto está adelantado.</li>
        <li>La línea vertical rosa indica la fecha actual.</li>
      </ul>
    </div>
  );
};

export default ChartInfo;