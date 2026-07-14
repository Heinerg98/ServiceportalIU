import React, { useEffect, useState } from 'react'
import axios from 'axios'

type ServiceRequest = {
  id: number,
  requesterName: string,
  requesterEmail: string,
  message: string,
  status: string,
  createdAt?: string
}

export default function RequestsAdmin(){
  const [requests, setRequests] = useState<ServiceRequest[]>([])
  const [loading, setLoading] = useState(false)

  useEffect(()=>{ fetchRequests() },[])

  async function fetchRequests(){
    setLoading(true)
    try{
      const res = await axios.get('/api/requests')
      setRequests(res.data)
    }catch(e){ console.error(e); alert('Fehler beim Laden der Anfragen') }
    setLoading(false)
  }

  async function updateStatus(id:number, status:string){
    try{
      const toUpdate = requests.find(r=>r.id===id)
      if(!toUpdate) return
      const body = { ...toUpdate, status }
      await axios.put(`/api/requests/${id}`, body)
      fetchRequests()
    }catch(e){ console.error(e); alert('Fehler beim Aktualisieren') }
  }

  if(loading) return <div>lade...</div>

  return (
    <div className="requests-admin">
      <h3>eingehende Anfragen</h3>
      {requests.length===0 && <div>Keine Anfragen vorhanden.</div>}
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Von</th>
            <th>Email</th>
            <th>Nachricht</th>
            <th>Status</th>
            <th>Aktionen</th>
          </tr>
        </thead>
        <tbody>
          {requests.map(r=> (
            <tr key={r.id}>
              <td>{r.id}</td>
              <td>{r.requesterName}</td>
              <td>{r.requesterEmail}</td>
              <td style={{maxWidth:300}}>{r.message}</td>
              <td>{r.status}</td>
              <td>
                <label>
                  <select aria-label={`Status für Anfrage ${r.id}`} value={r.status} onChange={e=>updateStatus(r.id, e.target.value)}>
                    <option>RECEIVED</option>
                    <option>IN_PROGRESS</option>
                    <option>DONE</option>
                    <option>CANCELLED</option>
                  </select>
                </label>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
