import './App.css'
import { Routes, Route } from 'react-router-dom';
import BigData from './pages/BigData'

function App() {
  return (
      <Routes>
        <Route path="/" element={<BigData />} />
      </Routes>
  )
}

export default App
