import React from 'react'
import { createRoot } from 'react-dom/client'
import App from './App'
import './index.css'
import axios from 'axios'

// Use relative URLs and rely on Vite proxy for /api during development
axios.defaults.baseURL = ''

// If auth exists in localStorage use it as Basic auth header
const stored = localStorage.getItem('auth')
if (stored) {
  axios.defaults.headers.common['Authorization'] = `Basic ${stored}`
}

createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
)
