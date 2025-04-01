// components/ChartControls.jsx
import React from 'react';
import { colors } from '../utils/projectData';

const ChartControls = ({ 
  selectedView, 
  handleViewChange, 
  selectedSprint, 
  handleSprintChange, 
  selectedMember, 
  handleMemberChange, 
  showIdeal, 
  setShowIdeal, 
  showActual, 
  setShowActual,
  sprints,
  members
}) => {
  return (
    <div className="w-full overflow-x-auto">
      <div className="flex flex-col md:flex-row md:justify-between md:items-center mb-4 gap-2">
        <div className="flex flex-wrap gap-2">
          <select 
            value={selectedView} 
            onChange={handleViewChange}
            className="border rounded p-2 bg-white text-black min-w-[150px] flex-grow md:flex-grow-0"
          >
            <option value="burndown">Burn-down Chart</option>
            <option value="workload">Distribución de Trabajo</option>
            <option value="status">Estado de Tareas</option>
            <option value="controlchart">Control Chart</option>
          </select>
          
          <select 
            value={selectedSprint} 
            onChange={handleSprintChange}
            className="border rounded p-2 bg-white text-black min-w-[150px] flex-grow md:flex-grow-0"
            disabled={!['burndown', 'controlchart'].includes(selectedView)}
          >
            {sprints.map(sprint => (
              <option key={sprint} value={sprint}>
                {sprint === 'all' ? 'Todos los Sprints' : sprint}
              </option>
            ))}
          </select>
          
          <select 
            value={selectedMember} 
            onChange={handleMemberChange}
            className="border rounded p-2 bg-white text-black min-w-[150px] flex-grow md:flex-grow-0"
            disabled={selectedView !== 'workload'}
          >
            {members.map(member => (
              <option key={member} value={member}>
                {member === 'all' ? 'Todos los Miembros' : member}
              </option>
            ))}
          </select>
        </div>
      </div>
      
      {selectedView === 'burndown' && (
        <div className="flex gap-4">
          <label className="flex items-center">
            <input 
              type="checkbox" 
              checked={showIdeal} 
              onChange={() => setShowIdeal(!showIdeal)}
              className="mr-2"
            />
            <span style={{ color: colors.ideal }}>Línea Ideal</span>
          </label>
          <label className="flex items-center">
            <input 
              type="checkbox" 
              checked={showActual} 
              onChange={() => setShowActual(!showActual)}
              className="mr-2"
            />
            <span style={{ color: colors.actual }}>Línea Real</span>
          </label>
        </div>
      )}
    </div>
  );
};

export default ChartControls;