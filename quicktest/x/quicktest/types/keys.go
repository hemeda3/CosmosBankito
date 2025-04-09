package types

const (
	// ModuleName defines the module name
	ModuleName = "quicktest"

	// StoreKey defines the primary module store key
	StoreKey = ModuleName

	// MemStoreKey defines the in-memory store key
	MemStoreKey = "mem_quicktest"
)

var (
	ParamsKey = []byte("p_quicktest")
)

func KeyPrefix(p string) []byte {
	return []byte(p)
}
