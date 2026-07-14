import React, { useEffect, useState } from 'react'
import axios from 'axios'

type User = { id: number, username: string, roles?: string }

export default function UsersAdmin(){
  const [users, setUsers] = useState<User[]>([])
  const [editingId, setEditingId] = useState<number|undefined>(undefined)
  const [roleInput, setRoleInput] = useState('')

  useEffect(()=>{ fetchUsers() },[])

  async function fetchUsers(){
    try{
      const res = await axios.get('/api/users')
      setUsers(res.data)
    }catch(e){ console.error(e); alert('Fehler beim Laden der Benutzer') }
  }

  function startEdit(u:User){ setEditingId(u.id); setRoleInput(u.roles||'ROLE_USER') }
  function cancel(){ setEditingId(undefined); setRoleInput('') }

  async function save(id:number){
    try{
      // API erwartet ein User-Objekt with roles
      await axios.put(`/api/users/${id}/roles`, { roles: roleInput })
      setEditingId(undefined)
      fetchUsers()
    }catch(e){ console.error(e); alert('Fehler beim Speichern') }
  }

  return (
    <div className="users-admin">
      <h3>Benutzerverwaltung</h3>
      <table>
        <thead>
          <tr><th>ID</th><th>Benutzername</th><th>Rollen</th><th>Aktionen</th></tr>
        </thead>
        <tbody>
          {users.map(u=> (
            <tr key={u.id}>
              <td>{u.id}</td>
              <td>{u.username}</td>
              <td>
                {editingId===u.id ? (
                  <input value={roleInput} onChange={e=>setRoleInput(e.target.value)} aria-label={`Rollen für ${u.username}`} />
                ) : (
                  <span>{u.roles}</span>
                )}
              </td>
              <td>
                {editingId===u.id ? (
                  <>
                    <button onClick={()=>save(u.id)}>Speichern</button>
                    <button onClick={cancel}>Abbrechen</button>
                  </>
                ) : (
                  <button onClick={()=>startEdit(u)}>Bearbeiten</button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
