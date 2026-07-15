import React, { useEffect, useState } from 'react'
import axios from 'axios'

// Typdefinition für ServiceRequest-Objekte
type ServiceRequest = {
  id: number,
  requesterName: string,
  requesterEmail: string,
  message: string,
  status: string,
  createdAt?: string
}

/**
 * RequestsAdmin-Komponente - WCAG 2.2 AA konform
 *
 * Beschreibung (Deutsch):
 * - Admin-UI zum Anzeigen und Ändern des Status eingehender Anfragen.
 * - Lädt die Anfragen per GET /api/requests (erfordert ADMIN-Rechte).
 * - Ermöglicht das Setzen des Status via PUT /api/requests/{id}.
 * - Mit verbesserter Fehlerbehandlung und ARIA-Labels.
 */
export default function RequestsAdmin(){
  const [requests, setRequests] = useState<ServiceRequest[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  useEffect(()=>{ fetchRequests() },[])

  async function fetchRequests(){
    setLoading(true)
    setError('')
    try{
      const res = await axios.get('/api/requests')
      setRequests(res.data)
    }catch(e){ 
      console.error(e)
      setError('Fehler beim Laden der Anfragen. Bitte versuchen Sie es später erneut.')
    }
    setLoading(false)
  }

  async function updateStatus(id:number, status:string){
    try{
      const toUpdate = requests.find(r=>r.id===id)
      if(!toUpdate) return
      const body = { ...toUpdate, status }
      await axios.put(`/api/requests/${id}`, body)
      setError('')
      fetchRequests()
    }catch(e){ 
      console.error(e)
      setError('Fehler beim Aktualisieren des Status.')
    }
  }

  if(loading) return (
    <div role="status" aria-live="polite" aria-busy="true">
      <span className="loading">Lade Anfragen...</span>
    </div>
  )

  return (
    <div className="requests-admin">
      <h3>Eingehende Anfragen</h3>
      
      {error && (
        <div role="alert" aria-live="assertive" className="error-message">
          {error}
        </div>
      )}
      
      {requests.length===0 && !error && <div role="status">Keine Anfragen vorhanden.</div>}
      
      {requests.length > 0 && (
        <table>
          <caption>Liste aller eingegangenen Service-Anfragen mit Status und Bearbeitungsmöglichkeiten</caption>
          <thead>
            <tr>
              <th scope="col">ID</th>
              <th scope="col">Von</th>
              <th scope="col">Email</th>
              <th scope="col">Nachricht</th>
              <th scope="col">Status</th>
              <th scope="col">Aktionen</th>
            </tr>
          </thead>
          <tbody>
            {requests.map(r=> (
              <tr key={r.id}>
                <td>{r.id}</td>
                <td>{r.requesterName}</td>
                <td><a href={`mailto:${r.requesterEmail}`}>{r.requesterEmail}</a></td>
                <td style={{maxWidth:'300px'}}>{r.message}</td>
                <td>{r.status}</td>
                <td>
                  <label htmlFor={`status-select-${r.id}`} style={{display: 'none'}}>Status für Anfrage {r.id}</label>
                  <select 
                    id={`status-select-${r.id}`}
                    value={r.status} 
                    onChange={e=>updateStatus(r.id, e.target.value)}
                    aria-label={`Status für Anfrage ${r.id} ändern`}
                  >
                    <option value="RECEIVED">RECEIVED</option>
                    <option value="IN_PROGRESS">IN_PROGRESS</option>
                    <option value="DONE">DONE</option>
                    <option value="CANCELLED">CANCELLED</option>
                  </select>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}
