import React, { useEffect, useState } from 'react'
import axios from 'axios'

// Typdefinition für Benutzerobjekte
type User = { id: number, username: string, roles?: string }

/**
 * UsersAdmin-Komponente - WCAG 2.2 AA konform
 *
 * Beschreibung (Deutsch):
 * - Admin-UI zur Auflistung und Bearbeitung von Benutzern.
 * - Lädt Benutzer per GET /api/users (Admin) und erlaubt das Ändern von Rollen.
 * - Mit verbesserter Fehlerbehandlung und ARIA-Labels.
 */
export default function UsersAdmin(){
  const [users, setUsers] = useState<User[]>([])
  const [editingId, setEditingId] = useState<number|undefined>(undefined)
  const [roleInput, setRoleInput] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  useEffect(()=>{ fetchUsers() },[])

  async function fetchUsers(){
    setLoading(true)
    setError('')
    try{
      const res = await axios.get('/api/users')
      setUsers(res.data)
    }catch(e){ 
      console.error(e)
      setError('Fehler beim Laden der Benutzer.')
    }
    setLoading(false)
  }

  function startEdit(u:User){ 
    setEditingId(u.id)
    setRoleInput(u.roles||'ROLE_USER')
    setError('')
  }
  
  function cancel(){ 
    setEditingId(undefined)
    setRoleInput('')
    setError('')
  }

  async function save(id:number){
    if (!roleInput.trim()) {
      setError('Rollen dürfen nicht leer sein')
      return
    }
    
    try{
      await axios.put(`/api/users/${id}/roles`, { roles: roleInput })
      setEditingId(undefined)
      setError('')
      fetchUsers()
    }catch(e){ 
      console.error(e)
      setError('Fehler beim Speichern der Rollen.')
    }
  }

  if (loading) return (
    <div role="status" aria-live="polite" aria-busy="true">
      <span className="loading">Lade Benutzer...</span>
    </div>
  )

  return (
    <div className="users-admin">
      <h3>Benutzerverwaltung</h3>
      
      {error && (
        <div role="alert" aria-live="assertive" className="error-message">
          {error}
        </div>
      )}
      
      {users.length === 0 && !error && <div role="status">Keine Benutzer vorhanden.</div>}
      
      {users.length > 0 && (
        <table>
          <caption>Liste aller Benutzer mit Rollenverwaltung</caption>
          <thead>
            <tr>
              <th scope="col">ID</th>
              <th scope="col">Benutzername</th>
              <th scope="col">Rollen</th>
              <th scope="col">Aktionen</th>
            </tr>
          </thead>
          <tbody>
            {users.map(u=> (
              <tr key={u.id}>
                <td>{u.id}</td>
                <td>{u.username}</td>
                <td>
                  {editingId===u.id ? (
                    <input 
                      value={roleInput} 
                      onChange={e=>setRoleInput(e.target.value)}
                      aria-label={`Rollen für ${u.username}`}
                    />
                  ) : (
                    <span>{u.roles}</span>
                  )}
                </td>
                <td>
                  {editingId===u.id ? (
                    <>
                      <button onClick={()=>save(u.id)} type="button">Speichern</button>
                      <button onClick={cancel} type="button">Abbrechen</button>
                    </>
                  ) : (
                    <button onClick={()=>startEdit(u)} type="button">Bearbeiten</button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}
