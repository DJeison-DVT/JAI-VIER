"use client"
import React, { useState } from 'react';
import { 
  projectData, 
  sprintData, 
  memberData, 
  fullBurndownData, 
  tasks, 
  sprints, 
  members 
} from "./utils/projectData";

// Importación de componentes
import ProjectHeader from './components/ProjectHeader';
import ChartControls from './components/ChartControls';
import BurnDownChart from './components/BurnDownChart';
import WorkloadChart from './components/WorkloadChart';
import StatusChart from './components/StatusChart';
import ControlChart from './components/ControlChart';
import TaskTable from './components/TaskTable';
import ChartInfo from './components/ChartInfo';

const JAIVIERBurndownChart = () => {
  // Estados para los filtros y la visualización
  const [selectedView, setSelectedView] = useState('burndown');
  const [selectedSprint, setSelectedSprint] = useState('all');
  const [selectedMember, setSelectedMember] = useState('all');
  const [showIdeal, setShowIdeal] = useState(true);
  const [showActual, setShowActual] = useState(true);
  const [chartData, setChartData] = useState(fullBurndownData);
  
  // Manejadores de eventos
  const handleViewChange = (event) => {
    setSelectedView(event.target.value);
  };
  
  const handleSprintChange = (event) => {
    const sprint = event.target.value;
    setSelectedSprint(sprint);
    
    if (sprint === 'all') {
      setChartData(fullBurndownData);
    } else {
      setChartData(fullBurndownData.filter(item => item.sprint === sprint));
    }
  };
  
  const handleMemberChange = (event) => {
    setSelectedMember(event.target.value);
  };

  // Renderizado condicional basado en la vista seleccionada
  const renderChart = () => {
    switch(selectedView) {
      case 'burndown':
        return (
          <BurnDownChart 
            chartData={chartData} 
            showIdeal={showIdeal} 
            showActual={showActual} 
          />
        );
      
      case 'workload':
        return (
          <WorkloadChart 
            memberData={memberData} 
            selectedMember={selectedMember} 
          />
        );
      
      case 'status':
        return (
          <StatusChart 
            projectData={projectData} 
          />
        );
      
      case 'controlchart':
        return (
          <ControlChart 
            tasks={tasks}
            selectedSprint={selectedSprint}
          />
        );
      
      default:
        return <div>Selecciona una vista</div>;
    }
  };

  // Función actualizada para incluir el control chart en los controles
  const getUpdatedControls = () => {
    return (
      <ChartControls 
        selectedView={selectedView}
        handleViewChange={handleViewChange}
        selectedSprint={selectedSprint}
        handleSprintChange={handleSprintChange}
        selectedMember={selectedMember}
        handleMemberChange={handleMemberChange}
        showIdeal={showIdeal}
        setShowIdeal={setShowIdeal}
        showActual={showActual}
        setShowActual={setShowActual}
        sprints={sprints}
        members={members}
      />
    );
  };

  return (
    <div className="bg-gray-100 p-6 rounded-lg shadow-lg">
      {/* Header del proyecto con información general */}
      <ProjectHeader 
        projectData={projectData} 
        sprintData={sprintData} 
      />

      {/* Controles para filtrar y configurar gráficos */}
      {getUpdatedControls()}

      {/* Gráfico basado en la vista seleccionada */}
      {renderChart()}

      {/* Tabla de tareas del sprint actual */}
      <TaskTable 
        tasks={tasks} 
        currentSprint={projectData.currentSprint} 
      />
      
      {/* Información sobre interpretación del gráfico burndown */}
      {selectedView === 'burndown' && <ChartInfo />}
    </div>
  );
};

export default JAIVIERBurndownChart;